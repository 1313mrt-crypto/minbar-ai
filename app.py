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

# CSS حرفه‌ای
st.markdown("""
<style>
    #MainMenu {visibility: hidden;}
    footer {visibility: hidden;}
    header {visibility: hidden;}

    .main {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 0;
    }

    .stApp {
        background: transparent;
    }

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

# Session State
if 'logged_in' not in st.session_state:
    st.session_state.logged_in = False
if 'user_plan' not in st.session_state:
    st.session_state.user_plan = 'free'
if 'speeches_count' not in st.session_state:
    st.session_state.speeches_count = 0

# توابع
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

def generate_speech(topic, num_points, duration_minutes, api_key):
    if not api_key:
        return None
    content_length = calculate_content_length(duration_minutes)
    prompt = f"""
    یک سخنرانی منبری حرفه‌ای درباره "{topic}" تولید کن.
    مدت: {duration_minutes} دقیقه، کلمات: {content_length['total_words']}
    
    JSON:
    {{
        "title": "عنوان",
        "introduction": "مقدمه",
        "points": [
            {{
                "number": 1,
                "title": "عنوان نکته",
                "content": "توضیح",
                "example": "مثال",
                "keywords": ["کلید"]
            }}
        ],
        "conclusion": "جمع‌بندی",
        "key_messages": ["پیام"]
    }}
    
    تعداد نکات: {num_points}
    """
    try:
        genai.configure(api_key=api_key)
        model = genai.GenerativeModel(
            model_name="gemini-2.0-flash-exp",
            generation_config={"temperature": 0.7, "response_mime_type": "application/json"}
        )
        time.sleep(2)
        response = model.generate_content(prompt)
        return json.loads(response.text)
    except Exception as e:
        st.error(f"❌ خطا: {str(e)}")
        return None

def create_powerpoint(speech_data, duration_minutes):
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
    pdf_io = io.BytesIO()
    doc = SimpleDocTemplate(pdf_io, pagesize=A4, rightMargin=72, leftMargin=72, topMargin=72, bottomMargin=18)
    story = []
    styles = getSampleStyleSheet()
    title_style = ParagraphStyle('CustomTitle', parent=styles['Heading1'], fontSize=24, textColor='#2c3e50', spaceAfter=30, alignment=TA_CENTER)
    heading_style = ParagraphStyle('CustomHeading', parent=styles['Heading2'], fontSize=16, textColor='#34495e', spaceAfter=12, alignment=TA_RIGHT)
    normal_style = ParagraphStyle('CustomNormal', parent=styles['Normal'], fontSize=12, leading=18, alignment=TA_RIGHT)
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

# UI
st.markdown("""
<div class="custom-header">
    <h1>🎤 منبر هوشمند</h1>
    <p>استودیوی تولید سخنرانی با هوش مصنوعی</p>
</div>
""", unsafe_allow_html=True)

tab1, tab2, tab3, tab4 = st.tabs(["🏠 خانه", "✨ تولید سخنرانی", "💎 پلن‌ها", "⚙️ تنظیمات"])

with tab1:
    st.markdown('<div class="feature-card">', unsafe_allow_html=True)
    col1, col2 = st.columns([2, 1])
    with col1:
        st.markdown("### 🚀 قابلیت‌ها")
        st.markdown("""
        - ✅ تولید سخنرانی سریع
        - ✅ خروجی متنوع
        - ✅ موبایل محور
        """)
    with col2:
        try:
            st.image("https://via.placeholder.com/300x200?text=Demo", use_column_width=True)
        except:
            st.info("📱 تصویر")
    st.markdown('</div>', unsafe_allow_html=True)
    st.markdown("---")
    col1, col2, col3 = st.columns(3)
    with col1:
        st.metric("👥 کاربران", "۱,۲۳۴")
    with col2:
        st.metric("📊 سخنرانی", "۵,۶۷۸")
    with col3:
        st.metric("⭐ رضایت", "۴.۸/۵")

with tab2:
    if st.session_state.user_plan == 'free':
        remaining = 20 - st.session_state.speeches_count
        progress = (st.session_state.speeches_count / 20) * 100
        st.markdown(f"""
        <div class="progress-bar">
            <div class="progress-fill" style="width: {progress}%;">
                {st.session_state.speeches_count}/20
            </div>
        </div>
        """, unsafe_allow_html=True)
        if remaining <= 5:
            st.warning(f"⚠️ {remaining} باقی مانده!")
    
    st.markdown("---")
    with st.form("speech_form"):
        col1, col2 = st.columns([2, 1])
        with col1:
            topic = st.text_input("📝 موضوع:", placeholder="مثال: صبر")
        with col2:
            duration = st.selectbox("⏱️ مدت:", [5, 10, 15, 20, 30, 45, 60])
        num_points = st.slider("🔢 نکات:", 3, 10, 5)
        est = calculate_content_length(duration)
        st.info(f"📊 {est['total_words']} کلمه | {duration} دقیقه")
        st.markdown("### 📦 خروجی:")
        col1, col2, col3 = st.columns(3)
        with col1:
            out_pptx = st.checkbox("📊 PowerPoint", value=True)
            out_pdf = st.checkbox("📄 PDF", value=True)
        with col2:
            out_audio = st.checkbox("🔊 صوت", value=False)
            out_chart = st.checkbox("📈 نمودار", value=False)
        with col3:
            out_infographic = st.checkbox("🎨 اینفو", value=False)
        api_key = st.text_input("🔑 API:", type="password", value=os.environ.get("GEMINI_API_KEY", ""))
        submitted = st.form_submit_button("🚀 تولید")
    
    if submitted:
        if not topic:
            st.error("❌ موضوع خالی!")
        elif not api_key:
            st.error("❌ API خالی!")
        elif st.session_state.user_plan == 'free' and st.session_state.speeches_count >= 20:
            st.error("❌ سهمیه تمام!")
        else:
            with st.spinner("⏳ ..."):
                speech_data = generate_speech(topic, num_points, duration, api_key)
                if speech_data:
                    st.success("✅ آماده!")
                    st.session_state.speeches_count += 1
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
                    st.markdown("---")
                    st.markdown("### 📥 دانلود")
                    cols = st.columns(3)
                    idx = 0
                    if out_pptx:
                        pptx = create_powerpoint(speech_data, duration)
                        with cols[idx % 3]:
                            st.download_button("📊 PPTX", pptx, f"{topic[:15]}.pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
                        idx += 1
                    if out_pdf:
                        pdf = create_pdf(speech_data, duration)
                        with cols[idx % 3]:
                            st.download_button("📄 PDF", pdf, f"{topic[:15]}.pdf", "application/pdf")

with tab3:
    st.markdown("### 💎 پلن‌ها")
    col1, col2, col3 = st.columns(3)
    with col1:
        st.markdown("""
        <div class="plan-card">
            <h3>🆓 رایگان</h3>
            <div class="plan-price">۰ تومان</div>
            <p>✅ ۲۰/ماه</p>
            <p>✅ PPTX + PDF</p>
        </div>
        """, unsafe_allow_html=True)
        if st.button("شروع", key="free"):
            st.session_state.user_plan = 'free'
            st.success("✅ فعال!")
    with col2:
        st.markdown("""
        <div class="plan-card premium">
            <h3>💎 پرمیوم</h3>
            <div class="plan-price">۲۹۹,۰۰۰</div>
            <p>✅ نامحدود</p>
            <p>✅ همه</p>
        </div>
        """, unsafe_allow_html=True)
        if st.button("خرید", key="premium", type="primary"):
            st.info("🔜 زودی")
    with col3:
        st.markdown("""
        <div class="plan-card">
            <h3>🚀 حرفه‌ای</h3>
            <div class="plan-price">۹۹۹,۰۰۰</div>
            <p>✅ همه Premium</p>
            <p>✅ API</p>
        </div>
        """, unsafe_allow_html=True)
        if st.button("تماس", key="pro"):
            st.info("📧 support@minbar-ai.ir")

with tab4:
    st.markdown("### ⚙️ تنظیمات")
    with st.form("settings_form"):
        st.markdown("#### 🌐 زبان")
        language = st.selectbox("زبان:", ["فارسی", "عربی", "English"])
        st.markdown("#### 🔔 اعلان")
        notifications = st.checkbox("Push", value=True)
        st.markdown("#### 🎨 ظاهر")
        theme = st.selectbox("تم:", ["روشن", "تیره"])
        saved = st.form_submit_button("💾 ذخیره")
        if saved:
            st.success("✅ ذخیره!")

st.markdown("---")
st.markdown("""
<div style="text-align: center; color: white; padding: 2rem;">
    <p>💡 ساخته با ❤️ توسط منبر هوشمند</p>
    <p>📧 support@minbar-ai.ir</p>
</div>
""", unsafe_allow_html=True)
