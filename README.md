AI Interviewer App
📌 Overview
The AI Interviewer is an Android-based application that simulates real interview scenarios using Artificial Intelligence. It generates dynamic questions, evaluates user answers, and provides personalized feedback.

🚀 Features
🔐 User Authentication using Firebase
🤖 AI-based Question Generation
📝 Answer Evaluation with Feedback
🎤 Voice Input for Answers
💡 Hint Generation
📊 Score Tracking
💻 Support for Theory and Coding Questions
🏗️ System Architecture
The system consists of:

Android Frontend
Flask Backend (Python)
AI Model (LLaMA via Ollama)
Firebase Database
RESTful APIs for communication
🛠️ Technologies Used
Frontend: Android (Java/XML)
Backend: Python (Flask)
AI Model: LLaMA 3 via Ollama
Database: Firebase
Networking: Retrofit (HTTP/JSON)
⚙️ Backend Setup
cd Backend
pip install -r requirements.txt
ollama run llama3:8b
python server
