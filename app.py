import streamlit as st
import json
import google.generativeai as genai
from openai import OpenAI  # ✨ جدید: برای GapGPT
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

# ✨ سایدبار با انتخاب AI Provider
with st.sidebar:
    st.title("⚙️ تنظیمات AI")
    
    ai_provider = st.selectbox(
        "🤖 انتخاب هوش مصنوعی:",
        ["GapGPT (توصیه می‌شود)", "Google Gemini"],
        help="GapGPT: GPT-5, Claude 4.5 | Gemini: رایگان"
    )
    
    if ai_provider == "GapGPT (توصیه می‌شود)":
        gapgpt_model = st.selectbox(
            "📦 مدل:",
            ["gpt-5", "claude-sonnet-4-5", "gemini-2.5-pro", "gpt-4o", "deepseek"],
            help="Claude 4.5 برای کدنویسی، GPT-5 برای خلاقیت"
        )
        
        api_key_gapgpt = st.text_input("🔑 API Key گپ‌جی‌پی‌تی:", type="password")
        
        if api_key_gapgpt:
            st.success(f"✅ {gapgpt_model} آماده!")
        else:
            st.warning("⚠️ API Key گپ‌جی‌پی‌تی نیاز است")
    
    else:
        api_key_gapgpt = None
        st.info("🔄 از Gemini استفاده می‌شود (با Fallback)")
    
    st.divider()
    st.info("""
**GapGPT چیه؟**
پلتفرم ایرانی با:
• GPT-5 (جدیدترین)
• Claude 4.5 (بهترین)
• Gemini Pro
• بدون تحریم 🇮🇷
    """)

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

# ✨ تابع جدید: تولید با GapGPT
def generate_speech_with_gapgpt(topic, duration_minutes, style, audience, resistance_level, api_key, model):
    """تولید سخنرانی با GapGPT API"""
    
    if not api_key:
        st.error("❌ کلید API گپ‌جی‌پی‌تی یافت نشد!")
        return None
    
    content_length = calculate_content_length(duration_minutes)
    
    # تعیین تعداد نکات
    if duration_minutes <= 10:
        num_points = 3
    elif duration_minutes <= 20:
        num_points = 5
    elif duration_minutes <= 30:
        num_points = 7
    else:
        num_points = 10
    
    resistance_guide = {
        "کم": "مخاطب آماده است. لحن ملایم.",
        "متوسط": "نیاز به دلیل و منطق.",
        "زیاد": "مقاومت شدید. داستان‌های قوی و دلایل محکم."
    }
    
    prompt = f"""یک سخنرانی منبری حرفه‌ای درباره "{topic}" تولید کن.

**مشخصات:**
- مدت: {duration_minutes} دقیقه (~{content_length['total_words']} کلمه)
- سبک: {style}
- مخاطب: {audience}
- مقاومت: {resistance_level} → {resistance_guide[resistance_level]}
- تعداد نکات: {num_points}

**فرمت JSON دقیق:**
{{
    "title": "عنوان جذاب",
    "introduction": "مقدمه الهام‌بخش ({content_length['intro_words']} کلمه)",
    "points": [
        {{
            "number": 1,
            "title": "عنوان نکته",
            "content": "توضیح کامل ({content_length['points_words'] // num_points} کلمه)",
            "example": "مثال واقعی",
            "keywords": ["کلمه1", "کلمه2", "کلمه3"]
        }}
    ],
    "conclusion": "جمع‌بندی ({content_length['conclusion_words']} کلمه)",
    "key_messages": ["پیام1", "پیام2", "پیام3"]
}}"""

    try:
        client = OpenAI(
            base_url='https://api.gapgpt.app/v1',
            api_key=api_key
        )
        
        st.info(f"🔄 در حال تولید با {model}...")
        
        response = client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": "تو متخصص سخنرانی منبری هستی. فقط JSON تولید می‌کنی."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.7,
            response_format={"type": "json_object"} if model.startswith("gpt") else None
        )
        
        content = response.choices[0].message.content
        result = json.loads(content)
        st.success(f"✅ محتوا با {model} تولید شد!")
        return result
        
    except Exception as e:
        st.error(f"❌ خطا در {model}: {str(e)}")
        return None

# ✅ تابع قبلی: تولید با Gemini (حفظ شد)
def generate_speech_with_fallback(topic, duration_minutes, style, audience, resistance_level):
    """تولید سخنرانی با Gemini Fallback Strategy"""
    
    api_key = os.environ.get("GEMINI_API_KEY", "")
    if not api_key:
        st.error("❌ کلید API Gemini یافت نشد!")
        return None
    
    content_length = calculate_content_length(duration_minutes)
    
    if duration_minutes <= 10:
        num_points = 3
    elif duration_minutes <= 20:
        num_points = 5
    elif duration_minutes <= 30:
        num_points = 7
    else:
        num_points = 10
    
    resistance_guide = {
        "کم": "مخاطب آماده پذیرش است. از لحن ملایم و تشویقی استفاده کن.",
        "متوسط": "مخاطب نیاز به دلیل و منطق دارد. از دلایل عقلی و قرآنی استفاده کن.",
        "زیاد": "مخاطب مقاومت شدید دارد. از داستان‌های تأثیرگذار، دلایل قوی و زبان محترمانه ولی قاطع استفاده کن."
    }
    
    prompt = f"""یک سخنرانی منبری حرفه‌ای درباره "{topic}" تولید کن.

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
    
    models_to_try = [
        ("gemini-2.0-flash-exp", "Gemini 2.0 Flash (رایگان)"),
        ("gemini-1.5-flash", "Gemini 1.5 Flash (رایگان)"),
        ("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B (سبک‌تر)")
    ]
    
    for model_name, model_label in models_to_try:
        try:
            st.info(f"🔄 در حال استفاده از {model_label}...")
            time.sleep(2)
            
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
    
    st.error("❌ تمام مدل‌های Gemini در دسترس نیستند.")
    return None

def create_powerpoint(speech_data, duration_minutes):
    """ساخت PowerPoint"""
    prs = Presentation()
    prs.slide_width = Inches(10)
    prs.slide_height = Inches(7.5)

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

    intro_slide = prs.slides.add_slide(prs.slide_layouts[1])
    intro_slide.shapes.title.text = "مقدمه"
    intro_slide.placeholders[1].text = speech_data['introduction']

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

# ورودی‌ها
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

# انتخاب خروجی‌ها
st.markdown("### 📦 انتخاب خروجی‌ها:")
col1, col2, col3, col4 = st.columns(4)

with col1:
    out_txt = st.checkbox("📝 متن خام", value=True)
    out_pptx = st.checkbox("📊 PowerPoint", value=True)

with col2:
    out_pdf = st.checkbox("📄 PDF", value=True)
    out_chart = st.checkbox("📈 نمودار", value=True)

with col3:
    out_checklist = st.checkbox("✅ چک‌لیست", value=True)
    out_audio = st.checkbox("🔊 نمونه صوتی", value=False)

with col4:
    out_infographic = st.checkbox("🎨 اینفوگرافیک", value=True)
    out_json = st.checkbox("💾 JSON خام", value=False)

if st.button("🚀 تولید سخنرانی", type="primary", use_container_width=True):
    if not topic:
        st.warning("⚠️ لطفاً موضوع را وارد کنید")
    else:
        with st.spinner("⏳ در حال تولید محتوا..."):
            # ✨ انتخاب AI بر اساس تنظیمات
            if ai_provider == "GapGPT (توصیه می‌شود)" and api_key_gapgpt:
                speech_data = generate_speech_with_gapgpt(
                    topic, duration, style, audience, resistance,
                    api_key_gapgpt, gapgpt_model
                )
            else:
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
                
                cols = st.columns(4)
                col_idx = 0
                
                if out_txt:
                    txt_file = create_raw_text(speech_data, duration)
                    with cols[col_idx % 4]:
                        st.download_button("📝 متن خام", txt_file, f"{topic[:15]}.txt", use_container_width=True)
                    col_idx += 1
                
                if out_pptx:
                    with st.spinner("📊 ساخت PowerPoint..."):
                        pptx_file = create_powerpoint(speech_data, duration)
                        with cols[col_idx % 4]:
                            st.download_button("📊 PowerPoint", pptx_file, f"{topic[:15]}.pptx", use_container_width=True)
                    col_idx += 1
                
                if out_pdf:
                    with st.spinner("📄 ساخت PDF..."):
                        pdf_file = create_pdf(speech_data, duration)
                        with cols[col_idx % 4]:
                            st.download_button("📄 PDF", pdf_file, f"{topic[:15]}.pdf", use_container_width=True)
                    col_idx += 1
                
                if out_chart:
                    with st.spinner("📈 ساخت نمودار..."):
                        chart_file = create_content_chart(speech_data, duration)
                        with cols[col_idx % 4]:
                            st.download_button("📈 نمودار", chart_file, f"نمودار_{topic[:15]}.png", use_container_width=True)
                    col_idx += 1
                
                if out_checklist:
                    checklist = create_checklist(speech_data)
                    with cols[col_idx % 4]:
                        st.download_button("✅ چک‌لیست", checklist, f"چک‌لیست_{topic[:15]}.txt", use_container_width=True)
                    col_idx += 1
                
                if out_audio:
                    with st.spinner("🔊 ساخت نمونه صوتی..."):
                        audio_file = create_audio_with_emotion(speech_data, duration)
                        if audio_file:
