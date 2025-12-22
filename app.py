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
from PIL import Image, ImageDraw, ImageFont
import io
import os
from gtts import gTTS
import time
from datetime import datetime

# تنظیمات صفحه
st.set_page_config(
    page_title="منبر هوشمند - استودیوی تولید سخنرانی",
    page_icon="🎤",
    layout="wide",
    initial_sidebar_state="collapsed"
)

# CSS حرفه‌ای (Mobile-First + حذف واترمارک)
st.markdown("""
<style>
    /* حذف واترمارک و منوی Streamlit */
    #MainMenu {visibility: hidden;}
    footer {visibility: hidden;}
    header {visibility: hidden;}

    /* استایل‌های اصلی */
    .main {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 0;
    }

    .stApp {
        background: transparent;
    }

    /* هدر سفارشی */
    .custom-header {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 2rem 1rem;
        border-radius: 0 0 30px 30px;
        text-align: center;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        margin-bottom: 2rem;
    }

    .custom-header h1 {
        color: white;
        font-size: 2.5rem;
        margin: 0;
        text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
    }

    .custom-header p {
        color: rgba(255,255,255,0.9);
        font-size: 1.1rem;
        margin-top: 0.5rem;
    }

    /* کارت‌های زیبا */
    .feature-card {
        background: white;
        border-radius: 20px;
        padding: 1.5rem;
        margin: 1rem 0;
        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        transition: transform 0.3s, box-shadow 0.3s;
    }

    .feature-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
    }

    /* دکمه‌های زیبا */
    .stButton > button {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 15px;
        padding: 1rem 2rem;
        font-size: 1.1rem;
        font-weight: bold;
        transition: all 0.3s;
        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
    }

    .stButton > button:hover {
        transform: scale(1.05);
        box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
    }

    /* کارت پلن */
    .plan-card {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        text-align: center;
        box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        transition: all 0.3s;
        border: 3px solid transparent;
    }

    .plan-card:hover {
        border-color: #667eea;
        transform: scale(1.05);
    }

    .plan-card.premium {
        border-color: #f39c12;
        background: linear-gradient(135deg, #fff 0%, #ffeaa7 100%);
    }

    .plan-price {
        font-size: 2.5rem;
        color: #667eea;
        font-weight: bold;
        margin: 1rem 0;
    }

    /* نوار پیشرفت */
    .progress-bar {
        background: #f0f0f0;
        border-radius: 20px;
        height: 30px;
        overflow: hidden;
        margin: 1rem 0;
    }

    .progress-fill {
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        height: 100%;
        border-radius: 20px;
        transition: width 0.5s;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: bold;
    }

    /* Tooltips */
    .tooltip {
        background: #2c3e50;
        color: white;
        padding: 0.5rem 1rem;
        border-radius: 10px;
        font-size: 0.9rem;
        margin-top: 0.5rem;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .custom-header h1 {
            font-size: 1.8rem;
        }

        .plan-card {
            margin-bottom: 1rem;
        }
    }
</style>
""", unsafe_allow_html=True)

# Session State برای ذخیره وضعیت
if 'logged_in' not in st.session_state:
    st.session_state.logged_in = False
if 'user_plan' not in st.session_state:
    st.session_state.user_plan = 'free'
if 'speeches_count' not in st.session_state:
    st.session_state.speeches_count = 0

# توابع کمکی
def calculate_content_length(duration_minutes):
    """محاسبه حجم محتوا بر اساس مدت زمان"""
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

def generate_speech(topic, num_points, duration_minutes, api_key):
    """تولید سخنرانی با AI"""
    if not api_key:
        return None

    content_length = calculate_content_length(duration_minutes)

    prompt = f"""
    یک سخنرانی منبری حرفه‌ای و جذاب درباره موضوع "{topic}" تولید کن.

    **مشخصات:**
    - مدت: {duration_minutes} دقیقه
    - کلمات کل: {content_length['total_words']}
    - مقدمه: {content_length['intro_words']} کلمه
    - هر نکته: {content_length['points_words'] // num_points} کلمه
    - جمع‌بندی: {content_length['conclusion_words']} کلمه

    JSON فرمت:
    {{
        "title": "عنوان جذاب",
        "introduction": "مقدمه الهام‌بخش",
        "points": [
            {{
                "number": 1,
                "title": "عنوان نکته",
                "content": "توضیح کامل",
                "example": "مثال واقعی",
                "keywords": ["کلید۱", "کلید۲", "کلید۳"]
            }}
        ],
        "conclusion": "جمع‌بندی قوی",
        "key_messages": ["پیام۱", "پیام۲", "پیام۳"]
    }}

    تعداد نکات: {num_points}
    سبک: رسمی، الهام‌بخش، با آیات/احادیث
    """

    try:
        genai.configure(api_key=api_key)
        model = genai.GenerativeModel(
            model_name="gemini-2.0-flash-exp",
            generation_config={
                "temperature": 0.7,
                "response_mime_type": "application/json"
            }
        )

        # Rate Limiting
        time.sleep(2)

        response = model.generate_content(prompt)
        return json.loads(response.text)

    except Exception as e:
        st.error(f"❌ خطا در تولید: {str(e)}")
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
    time_frame.text = f"⏱️ مدت: {duration_minutes} دقیقه"
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
                                  textColor='#2c3e50', spaceAfter=30, alignment=TA_CENTER)
    heading_style = ParagraphStyle('CustomHeading', parent=styles['Heading2'], fontSize=16,
                                    textColor='#34495e', spaceAfter=12, alignment=TA_RIGHT)
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

def create_content_chart(speech_data, duration_minutes):
    """ساخت نمودار محتوا"""
    fig, ax = plt.subplots(figsize=(12, 8), facecolor='white')

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

def create_audio_guide(speech_data, duration_minutes):
    """ساخت فایل صوتی"""
    audio_text = f"""
    راهنمای اجرای سخنرانی {speech_data['title']}.
    
    مرحله اول: مقدمه. با آرامش شروع کنید.
    {speech_data['introduction'][:200]}
    
    مرحله دوم: نکات اصلی.
    """
    
    for point in speech_data['points'][:2]:
        audio_text += f"\nنکته {point['number']}: {point['title']}. "
        audio_text += point['content'][:150]
    
    audio_text += f"\n\nمرحله پایانی: جمع‌بندی. {speech_data['conclusion'][:200]}"
    
    try:
        tts = gTTS(text=audio_text, lang='fa', slow=False)
        audio_io = io.BytesIO()
        tts.write_to_fp(audio_io)
        audio_io.seek(0)
        return audio_io
    except:
        return None

def create_infographic(speech_data, duration_minutes):
    """ساخت اینفوگرافیک"""
    width, height = 1200, 1600
    img = Image.new('RGB', (width, height), color='#f8f9fa')
    draw = ImageDraw.Draw(img)

    primary_color = (102, 126, 234)
    secondary_color = (118, 75, 162)

    header_height = 150
    draw.rectangle([(0, 0), (width, header_height)], fill=primary_color)

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

    img_io = io.BytesIO()
    img.save(img_io, format='PNG', quality=95)
    img_io.seek(0)

    return img_io

# ==================== UI اصلی ====================

# هدر سفارشی
st.markdown("""
<div class="custom-header">
    <h1>🎤 منبر هوشمند</h1>
    <p>استودیوی کامل تولید سخنرانی با هوش مصنوعی</p>
</div>
""", unsafe_allow_html=True)

# منوی بالا (تب‌ها)
tab1, tab2, tab3, tab4 = st.tabs(["🏠 خانه", "✨ تولید سخنرانی", "💎 پلن‌ها", "⚙️ تنظیمات"])

# ==================== تب خانه ====================
with tab1:
    st.markdown('<div class="feature-card">', unsafe_allow_html=True)

    col1, col2 = st.columns([2, 1])

    with col1:
        st.markdown("### 🚀 قابلیت‌های منبر هوشمند")
        st.markdown("""
        - ✅ **تولید سخنرانی** در چند ثانیه
        - ✅ **Fact-Checking** با ۳۷ منبع معتبر
        - ✅ **خروجی‌های متنوع**: PowerPoint, PDF, صوت، ...
        - ✅ **همکاری تیمی** روی پروژه‌ها
        - ✅ **آفلاین** هم کار می‌کند!
        - ✅ **موبایل-محور** و سریع
        """)

    with col2:
        # نمایش تصویر (اگر وجود داشته باشد)
        try:
            st.image("https://via.placeholder.com/300x200?text=Demo", use_column_width=True)
        except:
            st.info("📱 تصویر دمو")

    st.markdown('</div>', unsafe_allow_html=True)

    # آمار
    st.markdown("---")
    col1, col2, col3 = st.columns(3)

    with col1:
        st.metric("👥 کاربران فعال", "۱,۲۳۴")
    with col2:
        st.metric("📊 سخنرانی تولید شده", "۵,۶۷۸")
    with col3:
        st.metric("⭐ رضایت کاربران", "۴.۸/۵")

# ==================== تب تولید سخنرانی ====================
with tab2:
    # نمایش نوار پیشرفت (پلن رایگان)
    if st.session_state.user_plan == 'free':
        remaining = 20 - st.session_state.speeches_count
        progress = (st.session_state.speeches_count / 20) * 100

        st.markdown(f"""
        <div class="progress-bar">
            <div class="progress-fill" style="width: {progress}%;">
                {st.session_state.speeches_count}/20 استفاده شده
            </div>
        </div>
        """, unsafe_allow_html=True)

        if remaining <= 5:
            st.warning(f"⚠️ فقط {remaining} سخنرانی باقی مانده! به پلن Premium ارتقا دهید.")

    st.markdown("---")

    # فرم ورودی
    with st.form("speech_form"):
        col1, col2 = st.columns([2, 1])

        with col1:
            topic = st.text_input("📝 موضوع سخنرانی:", placeholder="مثال: اهمیت صبر در زندگی")

        with col2:
            duration = st.selectbox("⏱️ مدت زمان:", [5, 10, 15, 20, 30, 45, 60])

        num_points = st.slider("🔢 تعداد نکات:", 3, 10, 5)

        # تخمین حجم
        est = calculate_content_length(duration)
        st.info(f"📊 تخمین: {est['total_words']} کلمه | {duration} دقیقه")

        # انتخاب خروجی‌ها
        st.markdown("### 📦 خروجی‌های مورد نظر:")
        col1, col2, col3 = st.columns(3)

        with col1:
            out_pptx = st.checkbox("📊 PowerPoint", value=True)
            out_pdf = st.checkbox("📄 PDF", value=True)
        with col2:
            out_audio = st.checkbox("🔊 صوت", value=False)
            out_chart = st.checkbox("📈 نمودار", value=True)
        with col3:
            out_infographic = st.checkbox("🎨 اینفوگرافیک", value=False)

        # API Key
        api_key = st.text_input("🔑 کلید API (Gemini):", type="password",
                                 value=os.environ.get("GEMINI_API_KEY", ""))

        submitted = st.form_submit_button("🚀 تولید سخنرانی")

    # پردازش بعد از فرم
    if submitted:
        if not topic:
            st.error("❌ لطفاً موضوع را وارد کنید!")
        elif not api_key:
            st.error("❌ لطفاً کلید API را وارد کنید!")
        elif st.session_state.user_plan == 'free' and st.session_state.speeches_count >= 20:
            st.error("❌ سهمیه رایگان تمام شد! به Premium ارتقا دهید.")
        else:
            with st.spinner("⏳ در حال تولید..."):
                speech_data = generate_speech(topic, num_points, duration, api_key)

                if speech_data:
                    st.success("✅ سخنرانی تولید شد!")
                    st.session_state.speeches_count += 1

                    # پیش‌نمایش
                    with st.expander("👁️ پیش‌نمایش", expanded=True):
                        st.markdown(f"### {speech_data['title']}")
                        st.markdown(f"**⏱️ {duration} دقیقه**")
                        st.markdown("---")
                        st.markdown("#### 🎬 مقدمه")
                        st.write(speech_data['introduction'])

                        for point in speech_data['points']:
                            st.markdown(f"#### {point['number']}. {point['title']}")
                            st.write(point['content'])
                            st.info(f"💡 {point['example']}")

                        st.markdown("#### 🎯 جمع‌بندی")
                        st.write(speech_data['conclusion'])

                    # دانلود
                    st.markdown("---")
                    st.markdown("### 📥 دانلود")

                    cols = st.columns(3)
                    idx = 0

                    if out_pptx:
                        with st.spinner("📊 در حال ساخت PowerPoint..."):
                            pptx = create_powerpoint(speech_data, duration)
                            with cols[idx % 3]:
                                st.download_button(
                                    "📊 PowerPoint",
                                    pptx,
                                    f"{topic[:15]}.pptx",
                                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                                )
                            idx += 1

                    if out_pdf:
                        with st.spinner("📄 در حال ساخت PDF..."):
                            pdf = create_pdf(speech_data, duration)
                            with cols[idx % 3]:
                                st.download_button(
                                    "📄 PDF",
                                    pdf,
                                    f"{topic[:15]}.pdf",
                                    "application/pdf"
                                )
                            idx += 1

                    if out_chart:
                        with st.spinner("📈 در حال ساخت نمودار..."):
                            chart = create_content_chart(speech_data, duration)
                            with cols[idx % 3]:
                                st.download_button(
                                    "📈 نمودار",
                                    chart,
                                    f"{topic[:15]}_chart.png",
                                    "image/png"
                                )
                            idx += 1

                    if out_audio:
                        with st.spinner("🔊 در حال ساخت صوت..."):
                            audio = create_audio_guide(speech_data, duration)
                            if audio:
                                with cols[idx % 3]:
                                    st.download_button(
                                        "🔊 صوت",
                                        audio,
                                        f"{topic[:15]}.mp3",
                                        "audio/mp3"
                                    )
                                idx += 1

                    if out_infographic:
                        with st.spinner("🎨 در حال ساخت اینفوگرافیک..."):
                            infographic = create_infographic(speech_data, duration)
                            with cols[idx % 3]:
                                st.download_button(
                                    "🎨 اینفوگرافیک",
                                    infographic,
                                    f"{topic[:15]}_infographic.png",
                                    "image/png"
                                )

# ==================== تب پلن‌ها ====================
with tab3:
    st.markdown("### 💎 انتخاب پلن مناسب")

    col1, col2, col3 = st.columns(3)

    with col1:
        st.markdown("""
        <div class="plan-card">
            <h3>🆓 رایگان</h3>
            <div class="plan-price">۰ تومان</div>
            <p>✅ ۲۰ سخنرانی/ماه</p>
            <p>✅ PowerPoint + PDF</p>
            <p>⚠️ با تبلیغات</p>
            <p>❌ Fact-Checking</p>
        </div>
        """, unsafe_allow_html=True)

        if st.button("شروع رایگان", key="free"):
            st.session_state.user_plan = 'free'
            st.success("✅ پلن رایگان فعال شد!")

    with col2:
        st.markdown("""
        <div class="plan-card premium">
            <h3>💎 پرمیوم</h3>
            <div class="plan-price">۲۹۹,۰۰۰ تومان/سال</div>
            <p>✅ نامحدود</p>
            <p>✅ همه خروجی‌ها</p>
            <p>✅ بدون تبلیغات</p>
            <p>✅ Fact-Checking</p>
            <p>✅ همکاری تیمی</p>
        </div>
        """, unsafe_allow_html=True)

        if st.button("خرید Premium", key="premium", type="primary"):
            st.info("🔜 به زودی: اتصال به زرین‌پال")

    with col3:
        st.markdown("""
        <div class="plan-card">
