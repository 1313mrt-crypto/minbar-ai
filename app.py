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
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.enums import TA_RIGHT, TA_CENTER
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch
from PIL import Image, ImageDraw, ImageFont
import io
import os
from gtts import gTTS
import numpy as np

# تنظیم صفحه
st.set_page_config(
    page_title="منبر هوشمند - استودیوی کامل",
    page_icon="🎤",
    layout="wide"
)

# استایل CSS سفارشی
st.markdown("""
<style>
    .main-header {
        text-align: center;
        color: #2c3e50;
        padding: 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 10px;
        margin-bottom: 30px;
    }
    .feature-box {
        background-color: #f8f9fa;
        padding: 15px;
        border-radius: 10px;
        border-left: 4px solid #667eea;
        margin: 10px 0;
    }
</style>
""", unsafe_allow_html=True)

# سایدبار
with st.sidebar:
    st.title("⚙️ تنظیمات")
    
    # دریافت API Key
    api_key = st.text_input(
        "🔑 کلید API جمینای:",
        type="password",
        value=os.environ.get("GEMINI_API_KEY", ""),
        help="کلید API خود را از https://aistudio.google.com/app/apikey دریافت کنید"
    )
    
    if api_key:
        genai.configure(api_key=api_key)
        st.success("✅ API Key تنظیم شد!")
    else:
        st.warning("⚠️ لطفاً کلید API را وارد کنید")
    
    st.divider()
    
    # انتخاب خروجی‌ها
    st.markdown("### 📦 انتخاب خروجی‌ها")
    
    output_pptx = st.checkbox("📊 PowerPoint", value=True)
    output_pdf = st.checkbox("📄 PDF متن کامل", value=True)
    output_chart = st.checkbox("📈 نمودار محتوا", value=True)
    output_checklist = st.checkbox("✅ چک‌لیست کلمات کلیدی", value=True)
    output_audio = st.checkbox("🔊 نمونه صوتی", value=False, help="تولید فایل صوتی ممکن است زمان‌بر باشد")
    output_infographic = st.checkbox("🎨 اینفوگرافیک", value=True)
    
    st.divider()
    st.markdown("### 📌 راهنما")
    st.markdown("""
    1. موضوع سخنرانی را وارد کنید
    2. مدت زمان را تعیین کنید
    3. تعداد نکات را انتخاب کنید
    4. خروجی‌های مورد نظر را علامت بزنید
    5. دکمه تولید را بزنید
    """)
    
    st.divider()
    st.markdown("### ⏱️ راهنمای زمان‌بندی")
    st.info("""
    **۵ دقیقه:** کوتاه و مختصر
    **۱۰ دقیقه:** متوسط
    **۱۵ دقیقه:** استاندارد
    **۲۰+ دقیقه:** تفصیلی
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

def generate_speech(topic, num_points, duration_minutes):
    if not api_key:
        st.error("❌ لطفاً کلید API را در سایدبار وارد کنید")
        return None
    
    content_length = calculate_content_length(duration_minutes)
    
    prompt = f"""
    یک سخنرانی منبری حرفه‌ای و جذاب درباره موضوع "{topic}" تولید کن.
    
    **مشخصات سخنرانی:**
    - مدت زمان: {duration_minutes} دقیقه
    - تعداد کلمات کل: حدود {content_length['total_words']} کلمه
    - مقدمه: حدود {content_length['intro_words']} کلمه
    - هر نکته: حدود {content_length['points_words'] // num_points} کلمه
    - جمع‌بندی: حدود {content_length['conclusion_words']} کلمه
    
    خروجی باید دقیقاً به این فرمت JSON باشد:
    {{
        "title": "عنوان سخنرانی",
        "introduction": "مقدمه‌ای جذاب (حدود {content_length['intro_words']} کلمه)",
        "points": [
            {{
                "number": 1,
                "title": "عنوان نکته",
                "content": "توضیح کامل (حدود {content_length['points_words'] // num_points} کلمه)",
                "example": "مثال واقعی",
                "keywords": ["کلمه کلیدی ۱", "کلمه کلیدی ۲", "کلمه کلیدی ۳"]
            }}
        ],
        "conclusion": "جمع‌بندی قوی (حدود {content_length['conclusion_words']} کلمه)",
        "key_messages": ["پیام کلیدی ۱", "پیام کلیدی ۲", "پیام کلیدی ۳"]
    }}
    
    تعداد نکات: {num_points}
    سبک: رسمی، الهام‌بخش، با آیات و احادیث
    """
    
    try:
        model = genai.GenerativeModel(
            model_name="gemini-2.0-flash-exp",
            generation_config={
                "temperature": 0.7,
                "response_mime_type": "application/json"
            }
        )
        
        response = model.generate_content(prompt)
        return json.loads(response.text)
    
    except Exception as e:
        st.error(f"❌ خطا در تولید محتوا: {str(e)}")
        return None

# ۱. تابع ساخت PowerPoint
def create_powerpoint(speech_data, duration_minutes):
    prs = Presentation()
    prs.slide_width = Inches(10)
    prs.slide_height = Inches(7.5)
    
    # اسلاید عنوان
    title_slide = prs.slides.add_slide(prs.slide_layouts[6])
    background = title_slide.shapes.add_shape(1, 0, 0, prs.slide_width, prs.slide_height)
    background.fill.solid()
    background.fill.fore_color.rgb = (41, 128, 185)
    
    title_box = title_slide.shapes.add_textbox(Inches(1), Inches(2.5), Inches(8), Inches(1.5))
    title_frame = title_box.text_frame
    title_frame.text = speech_data['title']
    title_frame.paragraphs[0].font.size = Pt(44)
    title_frame.paragraphs[0].font.bold = True
    title_frame.paragraphs[0].font.color.rgb = (255, 255, 255)
    title_frame.paragraphs[0].alignment = PP_ALIGN.CENTER
    
    time_box = title_slide.shapes.add_textbox(Inches(1), Inches(4.5), Inches(8), Inches(0.5))
    time_frame = time_box.text_frame
    time_frame.text = f"⏱️ مدت زمان: {duration_minutes} دقیقه"
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

# ۲. تابع ساخت PDF
def create_pdf(speech_data, duration_minutes):
    pdf_io = io.BytesIO()
    doc = SimpleDocTemplate(pdf_io, pagesize=A4, rightMargin=72, leftMargin=72, topMargin=72, bottomMargin=18)
    
    story = []
    styles = getSampleStyleSheet()
    
    # استایل‌های فارسی (ساده - بدون فونت خاص)
    title_style = ParagraphStyle(
        'CustomTitle',
        parent=styles['Heading1'],
        fontSize=24,
        textColor='#2c3e50',
        spaceAfter=30,
        alignment=TA_CENTER
    )
    
    heading_style = ParagraphStyle(
        'CustomHeading',
        parent=styles['Heading2'],
        fontSize=16,
        textColor='#34495e',
        spaceAfter=12,
        alignment=TA_RIGHT
    )
    
    normal_style = ParagraphStyle(
        'CustomNormal',
        parent=styles['Normal'],
        fontSize=12,
        leading=18,
        alignment=TA_RIGHT
    )
    
    # عنوان
    story.append(Paragraph(speech_data['title'], title_style))
    story.append(Paragraph(f"⏱️ مدت زمان: {duration_minutes} دقیقه", normal_style))
    story.append(Spacer(1, 0.5*inch))
    
    # مقدمه
    story.append(Paragraph("مقدمه", heading_style))
    story.append(Paragraph(speech_data['introduction'], normal_style))
    story.append(Spacer(1, 0.3*inch))
    
    # نکات
    for point in speech_data['points']:
        story.append(Paragraph(f"{point['number']}. {point['title']}", heading_style))
        story.append(Paragraph(point['content'], normal_style))
        story.append(Paragraph(f"💡 مثال: {point['example']}", normal_style))
        story.append(Spacer(1, 0.2*inch))
    
    # جمع‌بندی
    story.append(PageBreak())
    story.append(Paragraph("جمع‌بندی", heading_style))
    story.append(Paragraph(speech_data['conclusion'], normal_style))
    
    doc.build(story)
    pdf_io.seek(0)
    return pdf_io

# ۳. تابع ساخت نمودار محتوا
def create_content_chart(speech_data, duration_minutes):
    fig, ax = plt.subplots(figsize=(12, 8), facecolor='white')
    
    # محورها
    segments = ['مقدمه'] + [f"نکته {i+1}" for i in range(len(speech_data['points']))] + ['جمع‌بندی']
    colors = ['#3498db', '#e74c3c', '#2ecc71', '#f39c12', '#9b59b6', '#1abc9c', '#34495e']
    
    y_positions = list(range(len(segments)))
    
    for i, (segment, color) in enumerate(zip(segments, colors)):
        rect = FancyBboxPatch((0, i-0.4), 10, 0.8, boxstyle="round,pad=0.1", 
                               facecolor=color, edgecolor='none', alpha=0.7)
        ax.add_patch(rect)
        ax.text(5, i, segment, ha='center', va='center', fontsize=14, 
                color='white', weight='bold', family='sans-serif')
    
    ax.set_xlim(-1, 11)
    ax.set_ylim(-1, len(segments))
    ax.axis('off')
    ax.set_title(f'ساختار سخنرانی - {duration_minutes} دقیقه', 
                 fontsize=18, weight='bold', pad=20, family='sans-serif')
    
    plt.tight_layout()
    
    chart_io = io.BytesIO()
    plt.savefig(chart_io, format='png', dpi=300, bbox_inches='tight')
    chart_io.seek(0)
    plt.close()
    
    return chart_io

# ۴. تابع ساخت چک‌لیست
def create_checklist(speech_data):
    checklist_text = f"""
📋 چک‌لیست کلمات کلیدی
{'='*50}

🎤 سخنرانی: {speech_data['title']}

{'='*50}

🎯 پیام‌های کلیدی:
"""
    
    if 'key_messages' in speech_data:
        for i, msg in enumerate(speech_data['key_messages'], 1):
            checklist_text += f"\n  ☐ {i}. {msg}"
    
    checklist_text += "\n\n" + "="*50 + "\n\n📌 کلمات کلیدی هر بخش:\n\n"
    
    # مقدمه
    checklist_text += "🎬 مقدمه:\n"
    intro_keywords = speech_data['introduction'].split()[:10]
    for kw in intro_keywords:
        checklist_text += f"  ☐ {kw}\n"
    
    # نکات
    for point in speech_data['points']:
        checklist_text += f"\n{point['number']}. {point['title']}:\n"
        if 'keywords' in point:
            for kw in point['keywords']:
                checklist_text += f"  ☐ {kw}\n"
    
    # جمع‌بندی
    checklist_text += "\n🎯 جمع‌بندی:\n"
    conclusion_keywords = speech_data['conclusion'].split()[:8]
    for kw in conclusion_keywords:
        checklist_text += f"  ☐ {kw}\n"
    
    checklist_text += "\n" + "="*50 + "\n\n💡 نکات تمرین:\n"
    checklist_text += "  • تمرین با صدای بلند\n"
    checklist_text += "  • رعایت زمان‌بندی\n"
    checklist_text += "  • تأکید روی کلمات کلیدی\n"
    checklist_text += "  • تماس چشمی با مخاطب\n"
    
    return checklist_text

# ۵. تابع ساخت فایل صوتی
def create_audio_guide(speech_data, duration_minutes):
    # متن راهنما
    audio_text = f"""
    راهنمای اجرای سخنرانی {speech_data['title']}.
    
    مرحله اول: مقدمه. با آرامش و فرود شروع کنید.
    {speech_data['introduction'][:200]}
    
    مرحله دوم: نکات اصلی. با انرژی و فراز ادامه دهید.
    """
    
    for point in speech_data['points'][:2]:  # فقط ۲ نکته اول
        audio_text += f"\nنکته {point['number']}: {point['title']}. "
        audio_text += point['content'][:150]
    
    audio_text += f"\n\nمرحله پایانی: جمع‌بندی. با فرود و آرامش به پایان برسانید. {speech_data['conclusion'][:200]}"
    
    try:
        tts = gTTS(text=audio_text, lang='fa', slow=False)
        audio_io = io.BytesIO()
        tts.write_to_fp(audio_io)
        audio_io.seek(0)
        return audio_io
    except:
        return None

# ۶. تابع ساخت اینفوگرافیک
def create_infographic(speech_data, duration_minutes):
    # ساخت تصویر
    width, height = 1200, 1600
    img = Image.new('RGB', (width, height), color='#f8f9fa')
    draw = ImageDraw.Draw(img)
    
    # رنگ‌ها
    primary_color = (102, 126, 234)
    secondary_color = (118, 75, 162)
    text_color = (44, 62, 80)
    
    # عنوان
    header_height = 150
    draw.rectangle([(0, 0), (width, header_height)], fill=primary_color)
    
    # نکات (ساده‌شده)
    y_offset = 200
    for i, point in enumerate(speech_data['points'], 1):
        box_y = y_offset + (i-1) * 250
        draw.rounded_rectangle(
            [(50, box_y), (width-50, box_y+200)],
            radius=20,
            fill='white',
            outline=secondary_color,
            width=3
        )
    
    # ذخیره
    img_io = io.BytesIO()
    img.save(img_io, format='PNG', quality=95)
    img_io.seek(0)
    
    return img_io

# رابط کاربری اصلی
st.markdown('<div class="main-header"><h1>🎤 منبر هوشمند - استودیوی کامل تولید محتوا</h1></div>', unsafe_allow_html=True)

col1, col2, col3 = st.columns([2, 1, 1])

with col1:
    topic = st.text_input("📝 موضوع سخنرانی:", placeholder="مثلاً: اهمیت صبر در زندگی")

with col2:
    duration_minutes = st.number_input("⏱️ مدت زمان (دقیقه):", min_value=5, max_value=60, value=15, step=5)

with col3:
    num_points = st.slider("🔢 تعداد نکات:", min_value=3, max_value=10, value=5)

estimated_words = calculate_content_length(duration_minutes)
st.info(f"📊 تخمین حجم محتوا: {estimated_words['total_words']} کلمه")

if st.button("🚀 تولید همه خروجی‌ها", type="primary", use_container_width=True):
    if not topic:
        st.warning("⚠️ لطفاً موضوع را وارد کنید")
    elif not api_key:
        st.error("❌ لطفاً کلید API را وارد کنید")
    else:
        with st.spinner(f"⏳ در حال تولید سخنرانی {duration_minutes} دقیقه‌ای..."):
            speech_data = generate_speech(topic, num_points, duration_minutes)
            
            if speech_data:
                st.success(f"✅ سخنرانی با موفقیت تولید شد!")
                
                # پیش‌نمایش
                with st.expander("👁️ پیش‌نمایش محتوا", expanded=True):
                    st.markdown(f"### {speech_data['title']}")
                    st.markdown(f"**⏱️ مدت زمان:** {duration_minutes} دقیقه")
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
                col_idx = 0
                
                # PowerPoint
                if output_pptx:
                    with st.spinner("📊 در حال ساخت PowerPoint..."):
                        pptx_file = create_powerpoint(speech_data, duration_minutes)
                        with cols[col_idx % 3]:
                            st.download_button(
                                "📊 PowerPoint",
                                pptx_file,
                                f"سخنرانی_{topic[:15]}.pptx",
                                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                                use_container_width=True
                            )
                        col_idx += 1
                
                # PDF
                if output_pdf:
                    with st.spinner("📄 در حال ساخت PDF..."):
                        pdf_file = create_pdf(speech_data, duration_minutes)
                        with cols[col_idx % 3]:
                            st.download_button(
                                "📄 PDF متن",
                                pdf_file,
                                f"متن_{topic[:15]}.pdf",
                                "application/pdf",
                                use_container_width=True
                            )
                        col_idx += 1
                
                # نمودار
                if output_chart:
                    with st.spinner("📈 در حال ساخت نمودار..."):
                        chart_file = create_content_chart(speech_data, duration_minutes)
                        with cols[col_idx % 3]:
                            st.download_button(
                                "📈 نمودار",
                                chart_file,
                                f"نمودار_{topic[:15]}.png",
                                "image/png",
                                use_container_width=True
                            )
                        col_idx += 1
                
                # چک‌لیست
                if output_checklist:
                    checklist_text = create_checklist(speech_data)
                    with cols[col_idx % 3]:
                        st.download_button(
                            "✅ چک‌لیست",
                            checklist_text,
                            f"چک‌لیست_{topic[:15]}.txt",
                            "text/plain",
                            use_container_width=True
                        )
                    col_idx += 1
                
                # صوت
                if output_audio:
                    with st.spinner("🔊 در حال ساخت فایل صوتی..."):
                        audio_file = create_audio_guide(speech_data, duration_minutes)
                        if audio_file:
                            with cols[col_idx % 3]:
                                st.download_button(
                                    "🔊 نمونه صوتی",
                                    audio_file,
                                    f"صوت_{topic[:15]}.mp3",
                                    "audio/mp3",
                                    use_container_width=True
                                )
                            col_idx += 1
                
                # اینفوگرافیک
                if output_infographic:
                    with st.spinner("🎨 در حال ساخت اینفوگرافیک..."):
                        infographic_file = create_infographic(speech_data, duration_minutes)
                        with cols[col_idx % 3]:
                            st.download_button(
                                "🎨 اینفوگرافیک",
                                infographic_file,
                                f"اینفوگرافیک_{topic[:15]}.png",
                                "image/png",
                                use_container_width=True
                            )

st.markdown("---")
st.markdown("💡 **نکته:** این استودیو با Gemini 2.0 Flash ساخته شده است | ⏱️ هر دقیقه ≈ ۱۳۰ کلمه")

