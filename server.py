from flask import Flask, request, jsonify
import requests
import random

app = Flask(__name__)
@app.route("/")
def home():
    return "Server is running successfully!"

OLLAMA_URL = "http://localhost:11434/api/generate"

ALLOWED_SKILLS = ["java", "python", "dsa", "c programming"]

last_questions = []  # prevent repetition


# -------- ASK AI --------
def ask_ai(prompt):
    try:
        response = requests.post(OLLAMA_URL, json={
            "model": "llama3:8b",
            "prompt": prompt,
            "stream": False
        })

        data = response.json()
        text = data.get("response", "").strip()

        # clean extra text
        if "\n" in text:
            text = text.split("\n")[0]

        return text

    except Exception as e:
        print("AI ERROR:", e)
        return None


# -------- GENERATE QUESTION --------
@app.route("/generate_question", methods=["POST"])
def generate_question():
    try:
        data = request.json
        skill = data.get("skill", "programming").lower()

        if skill not in ALLOWED_SKILLS:
            skill = "programming"

        # mix theory + coding
        q_type = random.choice(["theory", "coding"])

        prompt = f"""
{skill} interview question.
Type: {q_type}.
One question only.
No explanation.
Max 12 words.
"""

        question = ask_ai(prompt)

        # fallback if AI fails
        if not question or len(question) < 5:
            if q_type == "coding":
                question = f"Write a program to reverse a string in {skill}."
            else:
                question = f"What is a basic concept of {skill}?"

        # avoid repetition
        if question in last_questions:
            question = f"Explain one important concept in {skill}."

        last_questions.append(question)
        if len(last_questions) > 10:
            last_questions.pop(0)

        return jsonify({"question": question})

    except Exception as e:
        print("SERVER ERROR:", e)
        return jsonify({"question": "Error generating question"}), 500


# -------- EVALUATE ANSWER --------
import requests

@app.route("/evaluate_answer", methods=["POST"])
def evaluate_answer():
    data = request.json

    question = data.get("question", "")
    answer = data.get("answer", "")
    email = data.get("email", "")

    username = email.split("@")[0] if email else "User"

    if not answer.strip():
        return jsonify({
            "feedback": f"Hey {username}, please provide an answer.",
            "score": 0
        })

    prompt = f"""
You are an interview evaluator.

Question: {question}
Answer: {answer}

Evaluate strictly but fairly.

Return ONLY JSON like this:
{{
  "result": "CORRECT" or "PARTIAL" or "WRONG",
  "reason": "short reason"
}}

Rules:
- CORRECT → concept is right
- PARTIAL → somewhat correct but incomplete
- WRONG → unrelated or incorrect
- Always return valid JSON ONLY
"""

    try:
        response = requests.post(
            "http://localhost:11434/api/generate",
            json={
                "model": "llama3",
                "prompt": prompt,
                "stream": False
            },
            timeout=15
        )

        text = response.json().get("response", "").strip()

        # 🔥 CLEAN JSON (important)
        import json
        start = text.find("{")
        end = text.rfind("}") + 1
        json_text = text[start:end]

        result_data = json.loads(json_text)

        result = result_data.get("result", "WRONG")
        reason = result_data.get("reason", "")

        # 🎯 FINAL RESPONSE
        if result == "CORRECT":
            feedback = f"Hey {username}, Correct. "
            score = 10

        elif result == "PARTIAL":
            feedback = f"Hey {username}, Correct 👍 but you can improve: {reason}"
            score = 5   # partial gets some score

        else:
            feedback = f"Hey {username}, Incorrect. {reason}"
            score = 0

        return jsonify({
            "feedback": feedback,
            "score": score
        })

    except Exception as e:
        print("EVAL ERROR:", e)

        # 🔥 FALLBACK (SAFE + STRICT)
        common = set(question.lower().split()) & set(answer.lower().split())

        if len(common) >= 2:
            return jsonify({
                "feedback": f"Hey {username}, Correct 👍 (basic match)",
                "score": 5
            })
        else:
            return jsonify({
                "feedback": f"Hey {username}, Incorrect. Answer not related.",
                "score": 0
            })

#-------- HINT --------
@app.route("/get_hint", methods=["POST"])
def get_hint():
    try:
        data = request.json
        question = data.get("question", "")

        prompt = f"""
Give short hint.
Question: {question}
Max 10 words.
"""

        hint = ask_ai(prompt)

        if not hint:
            hint = "Think about basic concept."

        return jsonify({"hint": hint})

    except:
        return jsonify({"hint": "No hint available"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)