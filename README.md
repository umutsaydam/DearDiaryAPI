<div align="center"> 
  <span><a href="#backend-icerik">TR</a></span> • 
  <span><a href="#backend-content">EN</a></span> 
</div>

---

## 📋 İçindekiler

- [📡 Proje Hakkında](#proje-hakkında)
- [🛠 Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [📦 Kurulum](#kurulum)
- [🌐 API Endpointleri](#api-endpointleri)
- [🐳 Docker Kullanımı](#docker-kullanımı)
- [🤝 Katkıda Bulunma](#katkıda-bulunma)
- [📜 Lisans](#lisans)

---

## 📡 <span id="backend-icerik">DearDiary API – Günlüklerinizi Güvenle Saklayın</span>

DearDiaryAPI, kullanıcıların yazdığı günlükleri güvenli bir şekilde saklamayı ve metin analiziyle duygu durumlarını belirlemeyi amaçlayan bir RESTful API projesidir. Java Spring Boot ile yazılmış olup, Flask tabanlı ayrı bir mikroservis üzerinden makine öğrenmesi modelini çalıştırmaktadır.

## 🧠 <span id="proje-hakkında">Proje Hakkında</span>

Bu proje; günlük yazma alışkanlığı olan kullanıcıların ruh halini analiz edebilmesi, yazdığı içeriklerin güvenli şekilde saklanabilmesi ve ihtiyaç duyduğunda verilerini istatistiksel olarak görüntüleyebilmesi amacıyla geliştirilmiştir.

- **Spring Boot**: Kullanıcı işlemleri, günlük yönetimi ve JWT tabanlı kimlik doğrulama.
- **Flask**: Metin girdisinden duygusal analiz yaparak ['Sadness', 'Joy', 'Love', 'Anger', 'Fear', 'Surprise'] gibi sınıflar üretir ve indeks değerlerine göre cevap olarak tam sayı döndürür.
- **PostgreSQL**: Veritabanı.
- **Docker**: Hem Spring hem Flask için container yapıları.

### 📱 Mobile App

Bu backend'i kullanan mobil uygulamaya aşağıdaki bağlantıdan ulaşabilirsiniz:  
👉 [Mobile App](https://github.com/umutsaydam/DearDiaryApp)

## 🛠 <span id="kullanılan-teknolojiler">Kullanılan Teknolojiler</span>

- Java 17  
- Spring Boot 3.4.4  
- PostgreSQL  
- Spring Security + JWT  
- TestContainers (Unit Test)  
- Flask (Python 3.11)  
- Scikit-learn, NLTK (Flask servisindeki ML kütüphaneleri)  
- Docker & Docker Compose  
- ModelMapper, Lombok  

## 📦 <span id="kurulum">Kurulum</span>

1. Projeyi klonlayın:
   ```bash
   git clone https://github.com/umutsaydam/DearDiaryAPI.git
   cd DearDiaryAPI
   ```

2. Docker Compose ile çalıştırın:
   ```bash
   docker-compose up --build
   ```
   **Docker kullanılmayacaksa emotionAnalysisFromText klasöründeki requirements.txt kurulmalıdır.**
   ```bash
   pip install -r requirements. txt
   ```
   
   Arka planda:

   - Flask servis (`localhost:5000`) duygu analizi için çalışır  
   - Spring Boot backend (`localhost:8080`) API endpointlerini sağlar  
   - PostgreSQL (`localhost:5432`) veritabanı hazır olur  

## 🌐 <span id="api-endpointleri">API Endpointleri</span>

**Örnek istekler ve yanıtlar `Postman Collection` klasöründe mevcuttur.**

## 🐳 <span id="docker-kullanımı">Docker Kullanımı</span>

Proje, iki ana servisi içerir:

- **backend**: Java Spring Boot uygulaması  
- **ml-model**: Flask ile servis edilen duygu analizi modeli  

Tüm yapılar `docker-compose.yml` üzerinden tek komutla ayağa kaldırılabilir:

```bash
docker-compose up --build
```

## 🤝 <span id="katkıda-bulunma">Katkıda Bulunma</span>

Katkıda bulunmak istiyorsanız, bir **pull request** gönderin veya bir **issue** açın. Katkılarınız memnuniyetle karşılanacaktır!

## 📜 <span id="lisans">Lisans</span>

Lisans bilgileri için [LICENSE](LICENSE) dosyasına göz atabilirsiniz.

---

## 📋 Table of Contents

- [📡 About the Project](#about-the-project)
- [🛠 Technologies Used](#technologies-used)
- [📦 Installation](#installation)
- [🌐 API Endpoints](#api-endpoints)
- [🐳 Docker Usage](#docker-usage)
- [🤝 Contributing](#contributing)
- [📜 License](#license)

---

## 📡 <span id="backend-content">DearDiary API – Securely Store Your Diaries</span>

DearDiaryAPI is a RESTful API project designed to securely store users' diary entries and perform sentiment analysis on their texts. It is built with Java Spring Boot and runs a machine learning model through a separate microservice based on Flask.

## 🧠 <span id="about-the-project">About the Project</span>

This project was developed for users who have a habit of writing diaries to:

- Analyze their emotional state through their writings
- Store their contents securely
- View their data statistically when needed

- **Spring Boot**: Manages user operations, diary management, and JWT-based authentication.
- **Flask**: Performs sentiment analysis on text input and classifies it into categories such as ['Sadness', 'Joy', 'Love', 'Anger', 'Fear', 'Surprise'], returning an integer index as a response.
- **PostgreSQL**: Database.
- **Docker**: Container structures for both Spring and Flask services.

### 📱 Mobile App

You can access the mobile app that uses this backend service from the link below: 
👉 [Mobile App](https://github.com/umutsaydam/DearDiaryApp)

## 🛠 <span id="technologies-used">Technologies Used</span>

- Java 17  
- Spring Boot 3.4.4  
- PostgreSQL  
- Spring Security + JWT  
- TestContainers (Unit Test)  
- Flask (Python 3.11)  
- Scikit-learn, NLTK (ML libraries used in Flask service)  
- Docker & Docker Compose  
- ModelMapper, Lombok  

## 📦 <span id="installation">Installation</span>

1. Clone the project:
   ```bash
   git clone https://github.com/umutsaydam/DearDiaryAPI.git
   cd DearDiaryAPI

2. Run with Docker Compose:
   ```bash
   docker-compose up --build
   ```
   **If you don't want to use Docker, install the requirements.txt inside the emotionAnalysisFromText directory:**
   ```bash
   pip install -r requirements. txt
   ```

   In the background:

   - Flask service (localhost:5000) will handle sentiment analysis. 
   - Spring Boot backend (localhost:8080) will provide API endpoints.
   - PostgreSQL (localhost:5432) database will be ready.

🌐 <span id="api-endpoints">API Endpoints</span>
Sample requests and responses are available in the Postman Collection folder.

🐳 <span id="docker-usage">Docker Usage</span>
The project consists of two main services:

backend: Java Spring Boot application

ml-model: Sentiment analysis model served with Flask

You can bring up all services with a single command using docker-compose.yml:

```bash
docker-compose up --build
```

🤝 <span id="contributing">Contributing</span>
If you would like to contribute, please submit a pull request or open an issue. Contributions are warmly welcomed!

📜 <span id="license">License</span>
For license information, please refer to the LICENSE file.

---
