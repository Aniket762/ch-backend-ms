# CH – Backend Microservice

A Spring Boot–based backend service implementing a **custom Transformer-decoder chatbot**
for healthcare insurance guideline validation.

**Use Case**  
Doctors, clinics, and hospital staff query the system to verify whether a medical procedure
aligns with published insurance coverage guidelines (UnitedHealthcare), reducing claim denials.

---

## 📁 Project Folder Structure

```text
aniket762.combinehealth
├── api/
│   ├── TrainController.java
│   ├── ChatController.java
│   └── StatusController.java
│
├── service/
│   ├── ModelService.java
│   ├── TrainingService.java
│   └── InferenceService.java
│
├── nn/
│   ├── Config.java
│   ├── Matrix.java
│   ├── MatrixOps.java
│   ├── Embedding.java
│   ├── Attention.java
│   ├── MultiHeadAttention.java
│   ├── FeedForward.java
│   ├── DecoderBlock.java
│   └── TransformerDecoder.java
│
├── training/
│   ├── Trainer.java
│   ├── Loss.java
│   └── Optimizer.java
│
├── tokenizer/
│   ├── Tokenizer.java
│   └── BPETrainer.java
│
├── rag/
│   ├── DocumentChunk.java
│   ├── SimpleEmbedder.java
│   ├── VectorStore.java
│   └── Retriever.java
│
├── store/
│   ├── CheckpointManager.java
│   └── RagStore.java
│
├── startup/
│   └── StartupTrainer.java
│
├── util/
│   └── Utils.java
│
└── CombineHealthApplication.java

```
### 🧠 Model Design (Transformer Decoder)
```text
Input Tokens
   ↓
Token Embedding + Positional Encoding
   ↓
N × Decoder Block
   ├── Masked Multi-Head Self Attention
   ├── Add & LayerNorm
   ├── Feed Forward Network
   ├── Add & LayerNorm
   ↓
Linear Projection
   ↓
Softmax (Vocabulary Size)
```
### 🏗️ High-Level Architecture (HLD)
```text
┌────────────────────────┐
│        Client          │
│ (Web / Postman / UI)   │
└───────────┬────────────┘
            │ REST API
            ▼
┌────────────────────────┐
│ Spring Boot Controllers│
│  ├── /train            │
│  ├── /status           │
│  └── /ask              │
└───────────┬────────────┘
            ▼
┌──────────────────────────────┐
│        ModelService          │
│  (Training + Inference)      │
│                              │
│  ┌────────────────────────┐ │
│  │ Tokenizer (BPE)        │ │
│  └───────────┬────────────┘ │
│              ▼              │
│  ┌────────────────────────┐ │
│  │ Transformer Decoder    │ │
│  │ (Neural Core)          │ │
│  └───────────┬────────────┘ │
│              ▼              │
│  ┌────────────────────────┐ │
│  │ Trainer / Optimizer    │ │
│  └────────────────────────┘ │
│                              │
│  ┌────────────────────────┐ │
│  │ RAG Pipeline           │ │
│  │ (VectorStore + Search) │ │
│  └────────────────────────┘ │
└──────────────────────────────┘

```

### ⚙️ Low-Level Architecture (LLD – Decoder Internals)
```text
Input Tokens (int[])
      │
      ▼
┌────────────────────────┐
│ Token Embedding        │
│ + Positional Encoding │
└───────────┬────────────┘
            ▼
┌────────────────────────┐
│ Decoder Block × N      │
│                        │
│ ┌───────────────────┐ │
│ │ Masked Self-Attn  │ │
│ │ (Causal)          │ │
│ └─────────┬─────────┘ │
│           ▼           │
│ ┌───────────────────┐ │
│ │ Add + LayerNorm   │ │
│ └─────────┬─────────┘ │
│           ▼           │
│ ┌───────────────────┐ │
│ │ Feed Forward NN   │ │
│ │ Linear → ReLU →   │ │
│ │ Linear            │ │
│ └─────────┬─────────┘ │
│           ▼           │
│ ┌───────────────────┐ │
│ │ Add + LayerNorm   │ │
│ └───────────────────┘ │
└───────────┬────────────┘
            ▼
┌────────────────────────┐
│ Linear Projection      │
│ → Vocabulary Size     │
└───────────┬────────────┘
            ▼
      Token Probabilities

```
## 🔌 API Endpoints

The backend exposes REST APIs for **model training, status monitoring, and chatbot inference**.
All endpoints are documented via **Swagger (OpenAPI)**.


### 🤖 Chat API

**POST** `/api/agent/chat`  
Accepts a user query and returns a model-generated response using the trained
Transformer decoder and RAG pipeline.

- **Request Body:** Plain text question
- **Response:** Answer string



### 🧠 Model Management API

**POST** `/api/model/train`  
Triggers model training from an external guideline source.

- **Request Body:** `{ "url": "<guideline-article-url>" }`
- **Response:** `{ "status": "training_started" }`

**GET** `/api/model/status`  
Returns the current training state of the model.

- **Response:** `{ "status": "READY | TRAINING | FAILED", "error": "" }`


> Refer to the Swagger UI screenshot below for interactive API exploration.

<img width="1845" height="778" alt="image" src="https://github.com/user-attachments/assets/1c58f87b-384c-4daf-aa65-f7554810c8f2" />

## 💪CURL for Query -> Response
<img width="1586" height="571" alt="image" src="https://github.com/user-attachments/assets/35973206-db3d-4333-ad23-cb43ce804848" />


