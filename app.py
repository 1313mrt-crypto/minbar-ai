import streamlit as st
import json
from openai import OpenAI
from pptx import Presentation
from pptx.util import Inches
import io

# ═══════════════════════════════════════════════════════════
# تنظیمات اولیه
# ═══════════════════════════════════════════════════════════
st.set_page_config(
    page_title="🎤 دستیار هوشمند منبر",
    layout="wide",
    initial_sidebar_state="expanded"
)

# دریافت API Key از secrets یا input
if "openai" in st.secrets:
    default_api = st.secrets["openai"]["api_key"]
else:
    default_api = ""

if "api_key" not in st.session_state:
    st.session_state.api_key = default_api

# ═══════════════════════════════════════════════════════════
# سایدبار
# ═══════════════════════════════════════════════════════════
with st.sidebar:
    st.title("🎛 تنظیمات منبر")
    
    api_key = st.text_input(
        "🔑 کلید API OpenAI",
        type="password",
        value=st.session_state.api_key,
        help="اگر در Secrets تنظیم کردید، خودکار پر می‌شود"
    )
    st.session_state.api_key = api_key
    
    st.divider()
    
    topic = st.text_input("📚 موضوع سخنرانی", "حجاب و هویت")
    audience = st.selectbox("👥 مخاطب", ["نوجوان (نسل Z)", "جوانان دانشجو", "عموم مردم", "بازاریان"])
    tone = st.selectbox("🎭 لحن", ["حماسی و انگیزشی", "صمیمی و دوستانه", "منطقی و علمی", "احساسی و لطیف"])
    resistance = st.select_slider("⚡ میزان مقاومت", options=["همراه (موافق)", "بی‌تفاوت", "مخالف (گارد گرفته)"])
    
    st.divider()
    generate_btn = st.button("🚀 تولید سخنرانی", type="primary", use_container_width=True)

# ═══════════════════════════════════════════════════════════
# توابع
# ═══════════════════════════════════════════════════════════
def create_prompt(topic, audience, tone, resistance):
    return f"""
Role: You are a Grand Ayatollah, Top Psychologist, and Master Orator.

Create a structured speech in Persian based on:
- Topic: {topic}
- Audience: {audience}
- Tone: {tone}
- Resistance: {resistance}

Output ONLY valid JSON with exactly 5 steps:
{{
  "meta": {{"perspective": "...", "core_metaphor": "..."}},
  "critique_report": "...",
  "speech_content": [
    {{"step": "1. Motivation", "text": "...", "storyboard": "...", "slide_title": "...", "slide_bullet_points": ["..."]}},
    {{"step": "2. Problem", "text": "...", "storyboard": "...", "slide_title": "...", "slide_bullet_points": ["..."]}},
    {{"step": "3. Solution", "text": "...", "storyboard": "...", "slide_title": "...", "slide_bullet_points": ["..."]}},
    {{"step": "4. Proof", "text": "...", "storyboard": "...", "slide_title": "...", "slide_bullet_points": ["..."]}},
    {{"step": "5. Action", "text": "...", "storyboard": "...", "slide_title": "...", "slide_bullet_points": ["..."]}}
  ],
  "checklist": ["..."],
  "infographic_code": "graph TD\\n A[Start]-->B[End]"
}}
"""

def generate_speech(api_key, topic, audience, tone, resistance):
    try:
        client = OpenAI(api_key=api_key)
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": "You are a speech expert. Output only valid JSON."},
                {"role": "user", "content": create_prompt(topic, audience, tone, resistance)}
            ],
            response_format={"type": "json_object"},
            temperature=0.8
        )
        return json.loads(response.choices[0].message.content)
    except Exception as e:
        st.error(f"❌ خطا در تولید: {str(e)}")
        return None

def create_powerpoint(data, topic):
    prs = Presentation()
    
    # اسلاید عنوان
    title_slide = prs.slides.add_slide(prs.slide_layouts[0])
    title_slide.shapes.title.text = topic
    title_slide.placeholders[1].text = f"زاویه دید: {data['meta']['perspective']}"
    
    # اسلایدهای محتوا
    for section in data["speech_content"]:
        slide = prs.slides.add_slide(prs.slide_layouts[1])
        slide.shapes.title.text = section["slide_title"]
        
        tf = slide.placeholders[1].text_frame
        if section["slide_bullet_points"]:
            tf.text = section["slide_bullet_points"][0]
            for point in section["slide_bullet_points"][1:]:
                p = tf.add_paragraph()
                p.text = point
                p.level = 0
    
    ppt_io = io.BytesIO()
    prs.save(ppt_io)
    ppt_io.seek(0)
    return ppt_io

# ═══════════════════════════════════════════════════════════
# UI اصلی
# ═══════════════════════════════════════════════════════════
st.title("🎤 دستیار هوشمند منبر")
st.markdown("### تولید سخنرانی ساختاریافته با هوش مصنوعی")

if not st.session_state.api_key:
    st.warning("⚠️ لطفاً کلید API OpenAI را در سایدبار وارد کنید")
    st.info("💡 برای امنیت بیشتر، می‌توانید از قسمت Secrets در تنظیمات استریم‌لیت استفاده کنید")
    st.stop()

if generate_btn:
    with st.spinner('🧠 در حال تولید سخنرانی...'):
        data = generate_speech(st.session_state.api_key, topic, audience, tone, resistance)
    
    if data:
        st.session_state.speech_data = data
        st.success("✅ سخنرانی با موفقیت تولید شد!")
        st.balloons()

if "speech_data" in st.session_state:
    data = st.session_state.speech_data
    
    # گزارش منتقد
    with st.expander("🔍 گزارش تحلیل منتقد", expanded=False):
        st.warning(data["critique_report"])
    
    # متادیتا
    col1, col2 = st.columns(2)
    with col1:
        st.info(f"**🎯 زاویه دید:** {data['meta']['perspective']}")
    with col2:
        st.info(f"**🌟 استعاره اصلی:** {data['meta']['core_metaphor']}")
    
    st.divider()
    
    # متن سخنرانی
    st.header("📜 متن کامل سخنرانی")
    
    for idx, section in enumerate(data["speech_content"], 1):
        st.subheader(f"{section['step']}")
        
        col1, col2 = st.columns([2, 1])
        
        with col1:
            st.markdown(section["text"])
        
        with col2:
            st.success("**🎬 استوری‌بورد:**")
            st.caption(section["storyboard"])
        
        if idx < len(data["speech_content"]):
            st.divider()
    
    # چک‌لیست
    st.header("✅ چک‌لیست نهایی")
    for item in data["checklist"]:
        st.checkbox(item, value=False)
    
    st.divider()
    
    # دانلود
    st.header("📦 دانلود خروجی‌ها")
    
    col1, col2, col3 = st.columns(3)
    
    with col1:
        json_data = json.dumps(data, ensure_ascii=False, indent=2)
        st.download_button(
            "📄 دانلود JSON",
            json_data,
            file_name=f"speech_{topic[:20]}.json",
            mime="application/json",
            use_container_width=True
        )
    
    with col2:
        ppt_file = create_powerpoint(data, topic)
        st.download_button(
            "📊 دانلود پاورپوینت",
            ppt_file,
            file_name=f"speech_{topic[:20]}.pptx",
            mime="application/vnd.openxmlformats-officedocument.presentationml.presentation",
            use_container_width=True
        )
    
    with col3:
        text_content = "\n\n".join([
            f"{section['step']}\n{'='*50}\n{section['text']}" 
            for section in data["speech_content"]
        ])
        st.download_button(
            "📝 دانلود متن",
            text_content,
            file_name=f"speech_{topic[:20]}.txt",
            mime="text/plain",
            use_container_width=True
        )

st.caption("🤖 Powered by Claude Sonnet 4.5 & GPT-4o")
