import streamlit as st
import json
import google.generativeai as genai
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN
import io
import os

# تنظیم صفحه
st.set_page_config(page_title="منبر هوشمند", page_icon="🎤", layout="wide")

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
    st.markdown("### 📌 راهنما")
    st.markdown("""
    1. موضوع سخنرانی را وارد کنید
    2. تعداد نکات را انتخاب کنید
    3. دکمه تولید را بزنید
    4. PowerPoint را دانلود کنید
    """)

# تابع تولید سخنرانی
def generate_speech(topic, num_points):
    if not api_key:
        st.error("❌ لطفاً کلید API را در سایدبار وارد کنید")
        return None
    
    prompt = f"""
    یک سخنرانی منبری حرفه‌ای و جذاب درباره موضوع "{topic}" تولید کن.
    
    خروجی باید دقیقاً به این فرمت JSON باشد (بدون توضیح اضافی):
    {{
        "title": "عنوان سخنرانی",
        "introduction": "مقدمه‌ای جذاب و گیرا (۳-۴ جمله)",
        "points": [
            {{
                "number": 1,
                "title": "عنوان نکته اول",
                "content": "توضیح کامل نکته (۴-۵ جمله)",
                "example": "مثال واقعی و کاربردی"
            }}
        ],
        "conclusion": "جمع‌بندی قوی و الهام‌بخش"
    }}
    
    تعداد نکات: {num_points}
    سبک: رسمی، الهام‌بخش، با استفاده از آیات و احادیث
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

# تابع ساخت PowerPoint
def create_powerpoint(speech_data):
    prs = Presentation()
    prs.slide_width = Inches(10)
    prs.slide_height = Inches(7.5)
    
    # اسلاید عنوان
    title_slide = prs.slides.add_slide(prs.slide_layouts[6])
    
    # پس‌زمینه عنوان
    background = title_slide.shapes.add_shape(
        1, 0, 0, prs.slide_width, prs.slide_height
    )
    background.fill.solid()
    background.fill.fore_color.rgb = (41, 128, 185)
    
    # عنوان
    title_box = title_slide.shapes.add_textbox(
        Inches(1), Inches(3), Inches(8), Inches(1.5)
    )
    title_frame = title_box.text_frame
    title_frame.text = speech_data['title']
    title_frame.paragraphs[0].font.size = Pt(44)
    title_frame.paragraphs[0].font.bold = True
    title_frame.paragraphs[0].font.color.rgb = (255, 255, 255)
    title_frame.paragraphs[0].alignment = PP_ALIGN.CENTER
    
    # اسلاید مقدمه
    intro_slide = prs.slides.add_slide(prs.slide_layouts[1])
    intro_title = intro_slide.shapes.title
    intro_title.text = "مقدمه"
    intro_content = intro_slide.placeholders[1]
    intro_content.text = speech_data['introduction']
    
    # اسلایدهای نکات
    for point in speech_data['points']:
        slide = prs.slides.add_slide(prs.slide_layouts[1])
        
        title = slide.shapes.title
        title.text = f"{point['number']}. {point['title']}"
        
        content = slide.placeholders[1]
        text_frame = content.text_frame
        text_frame.clear()
        
        p1 = text_frame.paragraphs[0]
        p1.text = point['content']
        p1.level = 0
        
        p2 = text_frame.add_paragraph()
        p2.text = f"مثال: {point['example']}"
        p2.level = 1
    
    # اسلاید جمع‌بندی
    conclusion_slide = prs.slides.add_slide(prs.slide_layouts[1])
    conclusion_title = conclusion_slide.shapes.title
    conclusion_title.text = "جمع‌بندی"
    conclusion_content = conclusion_slide.placeholders[1]
    conclusion_content.text = speech_data['conclusion']
    
    # ذخیره در حافظه
    pptx_io = io.BytesIO()
    prs.save(pptx_io)
    pptx_io.seek(0)
    return pptx_io

# رابط کاربری اصلی
st.title("🎤 منبر هوشمند - تولید سخنرانی با Gemini")
st.markdown("---")

col1, col2 = st.columns([2, 1])

with col1:
    topic = st.text_input(
        "📝 موضوع سخنرانی:",
        placeholder="مثلاً: اهمیت صبر در زندگی"
    )

with col2:
    num_points = st.slider("🔢 تعداد نکات:", 3, 10, 5)

if st.button("🚀 تولید سخنرانی", type="primary", use_container_width=True):
    if not topic:
        st.warning("⚠️ لطفاً موضوع سخنرانی را وارد کنید")
    elif not api_key:
        st.error("❌ لطفاً کلید API را در سایدبار وارد کنید")
    else:
        with st.spinner("⏳ در حال تولید محتوا..."):
            speech_data = generate_speech(topic, num_points)
            
            if speech_data:
                st.success("✅ سخنرانی با موفقیت تولید شد!")
                
                # نمایش نتیجه
                with st.expander("👁️ پیش‌نمایش محتوا", expanded=True):
                    st.markdown(f"### {speech_data['title']}")
                    st.markdown("**مقدمه:**")
                    st.write(speech_data['introduction'])
                    
                    for point in speech_data['points']:
                        st.markdown(f"**{point['number']}. {point['title']}**")
                        st.write(point['content'])
                        st.info(f"💡 {point['example']}")
                    
                    st.markdown("**جمع‌بندی:**")
                    st.write(speech_data['conclusion'])
                
                # دکمه دانلود
                with st.spinner("📊 در حال ساخت PowerPoint..."):
                    pptx_file = create_powerpoint(speech_data)
                    
                    st.download_button(
                        label="📥 دانلود PowerPoint",
                        data=pptx_file,
                        file_name=f"سخنرانی_{topic[:20]}.pptx",
                        mime="application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        type="primary"
                    )

st.markdown("---")
st.markdown("💡 **نکته:** این ابزار با استفاده از Gemini 2.0 Flash ساخته شده است")
