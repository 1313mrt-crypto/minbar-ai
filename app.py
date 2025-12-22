import streamlit as st
import json
import google.generativeai as genai
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak
from reportlab.lib.enums import TA_RIGHT, TA_CENTER
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch
from PIL import Image, ImageDraw
import io
import os
from gtts import gTTS
import time

# تنظیمات صفحه
st.set_page_config(
    page_title="منبر هوشمند - استودیوی کامل",
    page_icon="🎤",
    layout="wide"
)

# استایل CSS
st.markdown("""
<style>
    .main-header {
        text-align: center;
        color: white;
        padding: 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 10px;
        margin-bottom: 30px;
    }
    .stButton > button {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        font-weight: bold;
        border: none;
        padding: 0.75rem 2rem;
        border-radius: 10px;
    }
</style>
""", unsafe_allow_html=True)

# توابع کمکی
def calculate_content_length(duration_minutes):
    words_per_minute = 130
    total_words = duration_minutes * words_per_minute
    intro_words = int(total_words * 0.15)
    conclusion_words = int(total_words * 0.15)
    points_words = total_words - intro_words - conclusion_words
    return {
        "intro_words": intro_words,
        "conclusion_words": conclusion_words,
        "points_words": points_words,
        "total_words": total_words
    }

def generate_speech_with_fallback(topic, duration_minutes, style, audience, resistance_level):
    """تولید سخنرانی با Fallback Strategy"""
    
    api_key = os.environ.get("GEMINI_API_KEY", "")
    if not api_key:
        st.error("❌ کلید API یافت نشد! لطفاً در تنظیمات Streamlit Cloud وارد کنید.")
        return None
    
    content_length = calculate_content_length(duration_minutes)
    
    # تعیین تعداد بخش‌ها بر اساس مدت زمان (AI خودکار)
    if duration_minutes <= 10:
        num_points = 3
    elif duration_minutes <= 20:
        num_points = 5
    elif duration_minutes <= 30:
        num_points = 7
    else:
        num_points = 10
    
    # راهنمای میزان مقاومت
    resistance_guide = {
        "کم": "مخاطب آماده پذیرش است. از لحن ملایم و تشویقی استفاده کن.",
        "متوسط": "مخاطب نیاز به دلیل و منطق دارد. از دلایل عقلی و قرآنی استفاده کن.",
        "زیاد": "مخاطب مقاومت شدید دارد. از داستان‌های تأثیرگذار، دلایل قوی و زبان محترمانه ولی قاطع استفاده کن."
    }
    
    prompt = f"""
    یک سخنرانی منبری حرفه‌ای درباره "{topic}" تولید کن.

    **مشخصات:**
    - مدت: {duration_minutes} دقیقه (~{content_length['total_words']} کلمه)
    - سبک: {style}
    - مخاطب: {audience}
    - میزان مقاومت مخاطب: {resistance_level}
    - راهنما: {resistance_guide[resistance_level]}
    - مقدمه: {content_length['intro_words']} کلمه
    - هر نکته: {content_length['points_words'] // num_points} کلمه
    - جمع‌بندی: {content_length['conclusion_words']} کلمه

    **فرمت JSON:**
    {{
        "title": "عنوان جذاب",
        "introduction": "مقدمه الهام‌بخش با توجه به میزان مقاومت",
        "points": [
            {{
                "number": 1,
                "title": "عنوان نکته",
                "content": "توضیح کامل",
                "example": "مثال واقعی",
                "keywords": ["کلمه1", "کلمه2", "کلمه3"]
            }}
        ],
        "conclusion": "جمع‌بندی قوی",
        "key_messages": ["پیام1", "پیام2", "پیام3"]
    }}

    تعداد نکات: {num_points}
    """
    
    # Fallback Strategy
    models_to_try = [
        ("gemini-2.0-flash-exp", "Gemini 2.0 Flash (رایگان)"),
        ("gemini-1.5-flash", "Gemini 1.5 Flash (رایگان)"),
        ("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B (سبک‌تر)")
    ]
    
    for model_name, model_label in models_to_try:
        try:
            st.info(f"🔄 در حال استفاده از {model_label}...")
            time.sleep(2)  # Rate Limiting
            
            genai.configure(api_key=api_key)
            model = genai.GenerativeModel(
                model_name=model_name,
                generation_config={
                    "temperature": 0.7,
                    "response_mime_type": "application/json"
                }
            )
            
            response = model.generate_content(prompt)
            result = json.loads(response.text)
            st.success(f"✅ محتوا با {model_label} تولید شد!")
            return result
            
        except Exception as e:
            error_msg = str(e)
            if "quota" in error_msg.lower() or "429" in error_msg:
                st.warning(f"⚠️ سهمیه {model_label} تمام شد. در حال امتحان مدل بعدی...")
                continue
            else:
                st.error(f"❌ خطا در {model_label}: {error_msg}")
                continue
    
    # اگر همه مدل‌های Gemini فیل شدند
    st.error("❌ تمام مدل‌های Gemini در دسترس نیستند. لطفاً بعداً امتحان کنید یا از Hugging Face استفاده کنید.")
    return None

def create_powerpoint(speech_data, duration_minutes):
    """ساخت PowerPoint"""
    prs = Presentation()
    prs.slide_width = Inches(10)
    prs.slide_height = Inches(7.5)

    # اسلاید عنوان
    title_slide = prs.slides.add_slide(prs.slide_layouts[6])
    background = title_slide.shapes.add_shape(1, 0, 0, prs.slide_width, prs.slide_height)
    background.fill.solid()
    background.fill.fore_color.rgb = (102, 126, 234)

    title_box = title_slide.shapes.add_textbox(Inches(1), Inches(2.5), Inches(8), Inches(1.5))
    title_frame = title_box.text_frame
    title_frame.text = speech_data['title']
    title_frame.paragraphs[0].font.size = Pt(44)
    title_frame.paragraphs[0].font.bold = True
    title_frame.paragraphs[0].font.color.rgb = (255, 255, 255)
    title_frame.paragraphs[0].alignment = PP_ALIGN.CENTER

    time_box = title_slide.shapes.add_textbox(Inches(1), Inches(4.5), Inches(8), Inches(0.5))
    time_frame = time_box.text_frame
    time_frame.text = f"⏱️ {duration_minutes} دقیقه"
    time_frame.paragraphs[0].font.size = Pt(24)
    time_frame.paragraphs[0].font.color.rgb = (255, 255, 255)
    time_frame.paragraphs[0].alignment = PP_ALIGN.CENTER

    # مقدمه
    intro_slide = prs.slides.add_slide(prs.slide_layouts[1])
    intro_slide.shapes.title.text = "مقدمه"
    intro_slide.placeholders[1].text = speech_data['introduction']

    # نکات
    for point in speech_data['points']:
        slide = prs.slides.add_slide(prs.slide_layouts[1])
        slide.shapes.title.text = f"{point['number']}. {point['title']}"
        content = slide.placeholders[1]
        text_frame = content.text_frame
        text_frame.clear()
        p1 = text_frame.paragraphs[0]
        p1.text = point['content']
        p2 = text_frame.add_paragraph()
        p2.text = f"💡 {point['example']}"
        p2.level = 1

    # جمع‌بندی
    conclusion_slide = prs.slides.add_slide(prs.slide_layouts[1])
    conclusion_slide.shapes.title.text = "جمع‌بندی"
    conclusion_slide.placeholders[1].text = speech_data['conclusion']

    pptx_io = io.BytesIO()
    prs.save(pptx_io)
    pptx_io.seek(0)
    return pptx_io

def create_pdf(speech_data, duration_minutes):
    """ساخت PDF"""
    pdf_io = io.BytesIO()
    doc = SimpleDocTemplate(pdf_io, pagesize=A4, rightMargin=72, leftMargin=72, topMargin=72, bottomMargin=18)
    story = []
    styles = getSampleStyleSheet()

    title_style = ParagraphStyle('CustomTitle', parent=styles['Heading1'], fontSize=24,
                                  spaceAfter=30, alignment=TA_CENTER)
    heading_style = ParagraphStyle('CustomHeading', parent=styles['Heading2'], fontSize=16,
                                    spaceAfter=12, alignment=TA_RIGHT)
    normal_style = ParagraphStyle('CustomNormal', parent=styles['Normal'], fontSize=12,
                                   leading=18, alignment=TA_RIGHT)

    story.append(Paragraph(speech_data['title'], title_style))
    story.append(Paragraph(f"⏱️ {duration_minutes} دقیقه", normal_style))
    story.append(Spacer(1, 0.5*inch))
    story.append(Paragraph("مقدمه", heading_style))
    story.append(Paragraph(speech_data['introduction'], normal_style))
    story.append(Spacer(1, 0.3*inch))

    for point in speech_data['points']:
        story.append(Paragraph(f"{point['number']}. {point['title']}", heading_style))
        story.append(Paragraph(point['content'], normal_style))
        story.append(Paragraph(f"💡 {point['example']}", normal_style))
        story.append(Spacer(1, 0.2*inch))

    story.append(PageBreak())
    story.append(Paragraph("جمع‌بندی", heading_style))
    story.append(Paragraph(speech_data['conclusion'], normal_style))

    doc.build(story)
    pdf_io.seek(0)
    return pdf_io

def create_raw_text(speech_data, duration_minutes):
    """متن خام TXT"""
    text = f"""
{'='*60}
{speech_data['title']}
⏱️ مدت زمان: {duration_minutes} دقیقه
{'='*60}

🎬 مقدمه:
{speech_data['introduction']}

{'='*60}
"""
    
    for point in speech_data['points']:
        text += f"""
{point['number']}. {point['title']}
{'-'*60}
{point['content']}

💡 مثال: {point['example']}

"""
    
    text += f"""
{'='*60}
🎯 جمع‌بندی:
{speech_data['conclusion']}
{'='*60}
"""
    return text

def create_content_chart(speech_data, duration_minutes):
    """نمودار محتوا"""
    fig, ax = plt.subplots(figsize=(12, 8), facecolor='white')

    segments = ['مقدمه'] + [f"نکته {i+1}" for i in range(len(speech_data['points']))] + ['جمع‌بندی']
    colors = ['#3498db', '#e74c3c', '#2ecc71', '#f39c12', '#9b59b6', '#1abc9c', '#34495e']

    for i, (segment, color) in enumerate(zip(segments, colors)):
        rect = FancyBboxPatch((0, i-0.4), 10, 0.8, boxstyle="round,pad=0.1",
                               facecolor=color, edgecolor='none', alpha=0.7)
        ax.add_patch(rect)
        ax.text(5, i, segment, ha='center', va='center', fontsize=14,
                color='white', weight='bold')

    ax.set_xlim(-1, 11)
    ax.set_ylim(-1, len(segments))
    ax.axis('off')
    ax.set_title(f'ساختار سخنرانی - {duration_minutes} دقیقه', fontsize=18, weight='bold', pad=20)

    plt.tight_layout()
    chart_io = io.BytesIO()
    plt.savefig(chart_io, format='png', dpi=300, bbox_inches='tight')
    chart_io.seek(0)
    plt.close()
    return chart_io

def create_checklist(speech_data):
    """چک‌لیست کلمات کلیدی"""
    checklist_text = f"""
📋 چک‌لیست کلمات کلیدی
{'='*60}

🎤 سخنرانی: {speech_data['title']}

{'='*60}

🎯 پیام‌های کلیدی:
"""
    if 'key_messages' in speech_data:
        for i, msg in enumerate(speech_data['key_messages'], 1):
            checklist_text += f"\n  ☐ {i}. {msg}"

    checklist_text += "\n\n" + "="*60 + "\n\n📌 کلمات کلیدی:\n\n"

    for point in speech_data['points']:
        checklist_text += f"\n{point['number']}. {point['title']}:\n"
        if 'keywords' in point:
            for kw in point['keywords']:
                checklist_text += f"  ☐ {kw}\n"

    checklist_text += "\n" + "="*60 + "\n\n💡 نکات اجرا:\n"
    checklist_text += "  • تمرین با صدای بلند\n"
    checklist_text += "  • رعایت فراز و فرود صدا\n"
    checklist_text += "  • تماس چشمی با مخاطب\n"
    return checklist_text

def create_audio_with_emotion(speech_data, duration_minutes):
    """نمونه صوتی با رعایت فراز و فرود"""
    
    # متن کامل با علامت‌گذاری فراز و فرود
    audio_text = f"""
    {speech_data['title']}.
    
    مقدمه.
    {speech_data['introduction'][:300]}
    """
    
    for i, point in enumerate(speech_data['points'][:3], 1):
        audio_text += f"\n\nنکته {i}: {point['title']}. "
        audio_text += point['content'][:200]
    
    audio_text += f"\n\nجمع‌بندی. {speech_data['conclusion'][:200]}"
    
    try:
        # تولید صوت (slow=False برای طبیعی‌تر شدن)
        tts = gTTS(text=audio_text, lang='fa', slow=False)
        audio_io = io.BytesIO()
        tts.write_to_fp(audio_io)
        audio_io.seek(0)
        return audio_io
    except Exception as e:
        st.warning(f"⚠️ خطا در تولید صوت: {e}")
        return None

def create_infographic(speech_data, duration_minutes):
    """اینفوگرافیک"""
    width, height = 1200, 1600
    img = Image.new('RGB', (width, height), color='#f8f9fa')
    draw = ImageDraw.Draw(img)

    primary_color = (102, 126, 234)
    header_height = 150
    draw.rectangle([(0, 0), (width, header_height)], fill=primary_color)

    y_offset = 200
    for i, point in enumerate(speech_data['points'], 1):
        box_y = y_offset + (i-1) * 200
        draw.rounded_rectangle(
            [(50, box_y), (width-50, box_y+150)],
            radius=20,
            fill='white',
            outline=primary_color,
            width=3
        )

    img_io = io.BytesIO()
    img.save(img_io, format='PNG', quality=95)
    img_io.seek(0)
    return img_io

# UI اصلی
st.markdown('<div class="main-header"><h1>🎤 منبر هوشمند</h1><p>استودیوی کامل تولید سخنرانی</p></div>', unsafe_allow_html=True)

col1, col2 = st.columns([3, 1])

with col1:
    topic = st.text_input("📝 موضوع سخنرانی:", placeholder="مثال: اهمیت صبر در زندگی")

with col2:
    duration = st.selectbox("⏱️ مدت زمان (دقیقه):", [5, 10, 15, 20, 30, 45, 60])

col3, col4, col5 = st.columns(3)

with col3:
    style = st.selectbox("🎨 سبک:", ["رسمی", "صمیمی", "آموزشی", "انگیزشی"])

with col4:
    audience = st.selectbox("👥 مخاطب:", ["عموم", "جوانان", "بانوان", "کودکان", "نخبگان"])

with col5:
    resistance = st.selectbox("⚡ میزان مقاومت:", ["کم", "متوسط", "زیاد"])

st.info(f"💡 تخمین: ~{calculate_content_length(duration)['total_words']} کلمه | AI تعداد بخش‌ها را خودکار تعیین می‌کند")

if st.button("🚀 تولید سخنرانی کامل", type="primary", use_container_width=True):
    if not topic:
        st.warning("⚠️ لطفاً موضوع را وارد کنید")
    else:
        with st.spinner("⏳ در حال تولید محتوا..."):
            speech_data = generate_speech_with_fallback(topic, duration, style, audience, resistance)
            
            if speech_data:
                # پیش‌نمایش
                with st.expander("👁️ پیش‌نمایش", expanded=True):
                    st.markdown(f"### {speech_data['title']}")
                    st.markdown(f"**⏱️ {duration} دقیقه | 🎨 {style} | 👥 {audience} | ⚡ مقاومت: {resistance}**")
                    st.markdown("---")
                    st.markdown("#### 🎬 مقدمه")
                    st.write(speech_data['introduction'])
                    
                    for point in speech_data['points']:
                        st.markdown(f"#### {point['number']}. {point['title']}")
                        st.write(point['content'])
                        st.info(f"💡 {point['example']}")
                    
                    st.markdown("#### 🎯 جمع‌بندی")
                    st.write(speech_data['conclusion'])
                
                # دانلود خروجی‌ها
                st.markdown("---")
                st.markdown("### 📥 دانلود خروجی‌ها")
                
                cols = st.columns(3)
                
                # متن خام
                with cols[0]:
                    txt_file = create_raw_text(speech_data, duration)
                    st.download_button("📝 متن خام (TXT)", txt_file, f"{topic[:15]}.txt", use_container_width=True)
                
                # PowerPoint
                with cols[1]:
                    with st.spinner("📊 ساخت PowerPoint..."):
                        pptx_file = create_powerpoint(speech_data, duration)
                        st.download_button("📊 PowerPoint", pptx_file, f"{topic[:15]}.pptx", use_container_width=True)
                
                # PDF
                with cols[2]:
                    with st.spinner("📄 ساخت PDF..."):
                        pdf_file = create_pdf(speech_data, duration)
                        st.download_button("📄 PDF", pdf_file, f"{topic[:15]}.pdf", use_container_width=True)
                
                cols2 = st.columns(3)
                
                # نمودار
                with cols2[0]:
                    with st.spinner("📈 ساخت نمودار..."):
                        chart_file = create_content_chart(speech_data, duration)
                        st.download_button("📈 نمودار", chart_file, f"نمودار_{topic[:15]}.png", use_container_width=True)
                
                # چک‌لیست
                with cols2[1]:
                    checklist = create_checklist(speech_data)
                    st.download_button("✅ چک‌لیست", checklist, f"چک‌لیست_{topic[:15]}.txt", use_container_width=True)
                
                # صوت
                with cols2[2]:
                    with st.spinner("🔊 ساخت نمونه صوتی..."):
                        audio_file = create_audio_with_emotion(speech_data, duration)
                        if audio_file:
                            st.download_button("🔊 نمونه صوتی", audio_file, f"صوت_{topic[:15]}.mp3", use_container_width=True)
                
                # اینفوگرافیک
                with st.spinner("🎨 ساخت اینفوگرافیک..."):
                    infographic = create_infographic(speech_data, duration)
                    st.download_button("🎨 اینفوگرافیک", infographic, f"اینفو_{topic[:15]}.png", use_container_width=True)
                
                # JSON خام
                st.download_button("💾 داده خام (JSON)", json.dumps(speech_data, ensure_ascii=False, indent=2),
                                   f"data_{topic[:15]}.json", use_container_width=True)

st.markdown("---")
st.markdown("💡 **نکته:** با Fallback Strategy، اگر سهمیه Gemini تمام شد، به مدل بعدی می‌رود | ⏱️ هر دقیقه ≈ 130 کلمه")
