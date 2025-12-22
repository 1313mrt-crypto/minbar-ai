import streamlit as st
import json
import google.generativeai as genai
from openai import OpenAI
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
    page_title="منبر هوشمند - استودیوی تولید سخنرانی",
    page_icon="🎤",
    layout="wide",
    initial_sidebar_state="expanded"
)

# CSS
st.markdown("""
<style>
    #MainMenu {visibility: hidden;}
    footer {visibility: hidden;}
    header {visibility: hidden;}
    
    .main {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 0;
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
    
    .stButton > button {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 15px;
        padding: 1rem 2rem;
        font-size: 1.1rem;
        font-weight: bold;
        transition: all 0.3s;
    }
</style>
""", unsafe_allow_html=True)

# Session State
if 'speeches_count' not in st.session_state:
    st.session_state.speeches_count = 0

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

def generate_speech_with_gapgpt(topic, num_points, duration_minutes, api_key, model):
    """تولید با GapGPT"""
    content_length = calculate_content_length(duration_minutes)
    
    prompt = f"""یک سخنرانی منبری حرفه‌ای درباره "{topic}" تولید کن.

مشخصات:
- مدت: {duration_minutes} دقیقه
- کلمات: {content_length['total_words']}
- نکات: {num_points}

JSON فرمت:
{{
    "title": "عنوان جذاب",
    "introduction": "مقدمه الهام‌بخش ({content_length['intro_words']} کلمه)",
    "points": [
        {{
            "number": 1,
            "title": "عنوان",
            "content": "توضیح ({content_length['points_words'] // num_points} کلمه)",
            "example": "مثال",
            "keywords": ["کلید1", "کلید2", "کلید3"]
        }}
    ],
    "conclusion": "جمع‌بندی ({content_length['conclusion_words']} کلمه)",
    "key_messages": ["پیام1", "پیام2", "پیام3"]
}}

سبک: رسمی، الهام‌بخش، با آیات/احادیث"""

    try:
        client = OpenAI(
            api_key=api_key,
            base_url="https://api.gapgpt.app/v1"
        )
        
        response = client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": "شما یک متخصص تولید محتوای منبری هستید."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.7,
            response_format={"type": "json_object"}
        )
        
        return json.loads(response.choices[0].message.content)
        
    except Exception as e:
        st.error(f"خطا در GapGPT: {str(e)}")
        return None

def generate_speech_with_gemini(topic, num_points, duration_minutes, api_key):
    """تولید با Gemini (Fallback)"""
    content_length = calculate_content_length(duration_minutes)
    
    prompt = f"""یک سخنرانی منبری حرفه‌ای درباره "{topic}" تولید کن.

مشخصات:
- مدت: {duration_minutes} دقیقه
- کلمات: {content_length['total_words']}
- نکات: {num_points}

JSON فرمت:
{{
    "title": "عنوان",
    "introduction": "مقدمه ({content_length['intro_words']} کلمه)",
    "points": [
        {{
            "number": 1,
            "title": "عنوان",
            "content": "محتوا ({content_length['points_words'] // num_points} کلمه)",
            "example": "مثال",
            "keywords": ["کلید1", "کلید2"]
        }}
    ],
    "conclusion": "نتیجه ({content_length['conclusion_words']} کلمه)",
    "key_messages": ["پیام1", "پیام2"]
}}"""

    gemini_models = [
        "gemini-2.0-flash-exp",
        "gemini-1.5-pro-latest",
        "gemini-1.5-flash-latest"
    ]
    
    for model_name in gemini_models:
        try:
            genai.configure(api_key=api_key)
            model = genai.GenerativeModel(
                model_name=model_name,
                generation_config={
                    "temperature": 0.7,
                    "response_mime_type": "application/json"
                }
            )
            
            time.sleep(2)
            response = model.generate_content(prompt)
            return json.loads(response.text)
            
        except Exception as e:
            st.warning(f"{model_name} ناموفق: {str(e)}")
            continue
    
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
    time_frame.text = f"مدت: {duration_minutes} دقیقه"
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
        p2.text = f"مثال: {point['example']}"
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
    story.append(Paragraph(f"مدت: {duration_minutes} دقیقه", normal_style))
    story.append(Spacer(1, 0.5*inch))
    story.append(Paragraph("مقدمه", heading_style))
    story.append(Paragraph(speech_data['introduction'], normal_style))
    story.append(Spacer(1, 0.3*inch))

    for point in speech_data['points']:
        story.append(Paragraph(f"{point['number']}. {point['title']}", heading_style))
        story.append(Paragraph(point['content'], normal_style))
        story.append(Paragraph(f"مثال: {point['example']}", normal_style))
        story.append(Spacer(1, 0.2*inch))

    story.append(PageBreak())
    story.append(Paragraph("جمع‌بندی", heading_style))
    story.append(Paragraph(speech_data['conclusion'], normal_style))

    doc.build(story)
    pdf_io.seek(0)
    return pdf_io

def create_content_chart(speech_data, duration_minutes):
    """نمودار"""
    fig, ax = plt.subplots(figsize=(12, 8), facecolor='white')
    
    segments = ['مقدمه'] + [f"نکته {i+1}" for i in range(len(speech_data['points']))] + ['جمع‌بندی']
    colors = ['#3498db', '#e74c3c', '#2ecc71', '#f39c12', '#9b59b6', '#1abc9c', '#34495e']
    
    for i, (segment, color) in enumerate(zip(segments, colors)):
        rect = FancyBboxPatch((0, i-0.4), 10, 0.8, boxstyle="round,pad=0.1",
                               facecolor=color, edgecolor='none', alpha=0.7)
        ax.add_patch(rect)
        ax.text(5, i, segment, ha='center', va='center', fontsize=14,
                color='white', weight='bold', family='sans-serif')
    
    ax.set_xlim(-1, 11)
    ax.set_ylim(-1, len(segments))
    ax.axis('off')
    ax.set_title(f'ساختار - {duration_minutes} دقیقه', fontsize=18, weight='bold', pad=20)
    
    plt.tight_layout()
    chart_io = io.BytesIO()
    plt.savefig(chart_io, format='png', dpi=300, bbox_inches='tight')
    chart_io.seek(0)
    plt.close()
    return chart_io

def create_checklist(speech_data):
    """چک‌لیست"""
    text = f"چک‌لیست - {speech_data['title']}\n\n"
    
    if 'key_messages' in speech_data:
        text += "پیام‌های کلیدی:\n"
        for i, msg in enumerate(speech_data['key_messages'], 1):
            text += f"{i}. {msg}\n"
    
    text += "\nکلمات کلیدی:\n\n"
    
    for point in speech_data['points']:
        text += f"{point['number']}. {point['title']}:\n"
        if 'keywords' in point:
            for kw in point['keywords']:
                text += f"  - {kw}\n"
        text += "\n"
    
    return text

def create_audio_guide(speech_data, duration_minutes):
    """صوت"""
    audio_text = f"راهنمای {speech_data['title']}. "
    audio_text += f"مقدمه: {speech_data['introduction'][:200]}. "
    
    for point in speech_data['points'][:2]:
        audio_text += f"نکته {point['number']}: {point['title']}. {point['content'][:150]}. "
    
    audio_text += f"جمع‌بندی: {speech_data['conclusion'][:200]}"
    
    try:
        tts = gTTS(text=audio_text, lang='fa', slow=False)
        audio_io = io.BytesIO()
        tts.write_to_fp(audio_io)
        audio_io.seek(0)
        return audio_io
    except Exception as e:
        st.warning(f"خطا در ساخت صوت: {str(e)}")
        return None

def create_infographic(speech_data, duration_minutes):
    """اینفوگرافیک"""
    width, height = 1200, 1600
    img = Image.new('RGB', (width, height), color='#f8f9fa')
    draw = ImageDraw.Draw(img)
    
    # هدر
    draw.rectangle([(0, 0), (width, 150)], fill=(102, 126, 234))
    
    # نکات
    y_offset = 200
    for i, point in enumerate(speech_data['points'], 1):
        box_y = y_offset + (i-1) * 250
        draw.rounded_rectangle(
            [(50, box_y), (width-50, box_y+200)],
            radius=20,
            fill='white',
            outline=(118, 75, 162),
            width=3
        )
    
    img_io = io.BytesIO()
    img.save(img_io, format='PNG', quality=95)
    img_io.seek(0)
    return img_io

# ==================== سایدبار ====================
with st.sidebar:
    st.title("تنظیمات")
    
    # انتخاب AI
    ai_provider = st.selectbox(
        "ارائه‌دهنده AI:",
        ["GapGPT (توصیه می‌شود)", "Google Gemini"]
    )
    
    # تنظیمات GapGPT
    if "GapGPT" in ai_provider:
        gapgpt_model = st.selectbox(
            "مدل GapGPT:",
            ["claude-sonnet-4-5", "gpt-4o", "gemini-2.5-pro", "grok-2"]
        )
        gapgpt_key = st.text_input("کلید GapGPT:", type="password")
        
        if gapgpt_key:
            st.success("GapGPT آماده است!")
    
    # تنظیمات Gemini
    else:
        gemini_key = st.text_input("کلید Gemini:", type="password",
                                    value=os.environ.get("GEMINI_API_KEY", ""))
        if gemini_key:
            st.success("Gemini آماده است!")
    
    st.divider()
    
    # خروجی‌ها
    st.markdown("### خروجی‌ها")
    output_pptx = st.checkbox("PowerPoint", value=True)
    output_pdf = st.checkbox("PDF", value=True)
    output_chart = st.checkbox("نمودار", value=True)
    output_checklist = st.checkbox("چک‌لیست", value=True)
    output_audio = st.checkbox("صوت", value=False)
    output_infographic = st.checkbox("اینفوگرافیک", value=True)

# ==================== UI اصلی ====================
st.markdown("""
<div class="custom-header">
    <h1>منبر هوشمند</h1>
    <p>استودیوی کامل تولید سخنرانی</p>
</div>
""", unsafe_allow_html=True)

col1, col2, col3 = st.columns([2, 1, 1])

with col1:
    topic = st.text_input("موضوع:", placeholder="مثال: اهمیت صبر")

with col2:
    duration = st.selectbox("مدت (دقیقه):", [5, 10, 15, 20, 30, 45, 60])

with col3:
    num_points = st.slider("تعداد نکات:", 3, 10, 5)

est = calculate_content_length(duration)
st.info(f"تخمین: {est['total_words']} کلمه")

if st.button("تولید سخنرانی", type="primary", use_container_width=True):
    if not topic:
        st.error("موضوع را وارد کنید!")
    elif "GapGPT" in ai_provider and not gapgpt_key:
        st.error("کلید GapGPT را وارد کنید!")
    elif "Gemini" in ai_provider and not gemini_key:
        st.error("کلید Gemini را وارد کنید!")
    else:
        with st.spinner("در حال تولید..."):
            speech_data = None
            
            # تولید با GapGPT
            if "GapGPT" in ai_provider:
                st.info(f"در حال تولید با {gapgpt_model}...")
                speech_data = generate_speech_with_gapgpt(topic, num_points, duration, gapgpt_key, gapgpt_model)
                
                if speech_data:
                    st.success(f"محتوا با {gapgpt_model} تولید شد!")
                else:
                    st.warning("GapGPT ناموفق، تلاش با Gemini...")
                    if 'gemini_key' in locals():
                        speech_data = generate_speech_with_gemini(topic, num_points, duration, gemini_key)
            
            # تولید با Gemini
            else:
                speech_data = generate_speech_with_gemini(topic, num_points, duration, gemini_key)
            
            if speech_data:
                st.session_state.speeches_count += 1
                
                # پیش‌نمایش
                with st.expander("پیش‌نمایش", expanded=True):
                    st.markdown(f"### {speech_data['title']}")
                    st.markdown(f"**مدت: {duration} دقیقه**")
                    st.markdown("---")
                    st.markdown("#### مقدمه")
                    st.write(speech_data['introduction'])
                    
                    for point in speech_data['points']:
                        st.markdown(f"#### {point['number']}. {point['title']}")
                        st.write(point['content'])
                        st.info(f"مثال: {point['example']}")
                    
                    st.markdown("#### جمع‌بندی")
                    st.write(speech_data['conclusion'])
                
                # دانلود
                st.markdown("---")
                st.markdown("### دانلود")
                
                cols = st.columns(3)
                idx = 0
                
                if output_pptx:
                    pptx = create_powerpoint(speech_data, duration)
                    with cols[idx % 3]:
                        st.download_button(
                            "PowerPoint",
                            pptx,
                            f"{topic[:15]}.pptx",
                            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                            use_container_width=True
                        )
                    idx += 1
                
                if output_pdf:
                    pdf = create_pdf(speech_data, duration)
                    with cols[idx % 3]:
                        st.download_button(
                            "PDF",
                            pdf,
                            f"{topic[:15]}.pdf",
                            "application/pdf",
                            use_container_width=True
                        )
                    idx += 1
                
                if output_chart:
                    chart = create_content_chart(speech_data, duration)
                    with cols[idx % 3]:
                        st.download_button(
                            "نمودار",
                            chart,
                            f"نمودار_{topic[:15]}.png",
                            "image/png",
                            use_container_width=True
                        )
                    idx += 1
                
                if output_checklist:
                    checklist = create_checklist(speech_data)
                    with cols[idx % 3]:
                        st.download_button(
                            "چک‌لیست",
                            checklist,
                            f"چک‌لیست_{topic[:15]}.txt",
                            "text/plain",
                            use_container_width=True
                        )
                    idx += 1
                
                if output_audio:
                    with st.spinner("ساخت صوت..."):
                        audio = create_audio_guide(speech_data, duration)
                        if audio:
                            with cols[idx % 3]:
                                st.download_button(
                                    "صوت",
                                    audio,
                                    f"صوت_{topic[:15]}.mp3",
                                    "audio/mp3",
                                    use_container_width=True
                                )
                            idx += 1
                
                if output_infographic:
                    infographic = create_infographic(speech_data, duration)
                    with cols[idx % 3]:
                        st.download_button(
                            "اینفوگرافیک",
                            infographic,
                            f"اینفوگرافیک_{topic[:15]}.png",
                            "image/png",
                            use_container_width=True
                        )
            else:
                st.error("تولید ناموفق بود!")

st.markdown("---")
st.markdown(f"ساخته شده با GapGPT & Gemini | استفاده: {st.session_state.speeches_count}")
