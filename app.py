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
        "زیاد": "مخاقاومت شدید دارد. از داستان‌های تأثیرگذار، دلایل قوی و زبان محترمانه ولی قاطع استفاده کن."
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
        "introduction":
