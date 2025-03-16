# 🤖 AI Vision - Détection et Classification d'Objets au Gabon

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Made in Gabon](https://img.shields.io/badge/Made%20in-Gabon-green?style=flat&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAzIDIiPjxwYXRoIGZpbGw9IiMwMDk4M2EiIGQ9Ik0wIDBoM3YySDB6Ii8+PHBhdGggZmlsbD0iI2ZmYzQwMCIgZD0iTTAgLjY2N2gzdjAuNjY2SDB6Ii8+PHBhdGggZmlsbD0iIzAwMDA5OCIgZD0iTTAgMS4zMzNoM3YwLjY2NkgweiIvPjwvc3ZnPg==)](https://github.com/yourusername/opencv-talk)

## 📝 Description

AI Vision est une application web innovante de détection et classification d'objets, développée au Gabon. Elle utilise
des technologies d'intelligence artificielle de pointe pour analyser des images et identifier des objets avec précision.
Cette solution s'inscrit dans la dynamique de transformation numérique du Gabon et vise à démocratiser l'accès aux
technologies d'IA en Afrique centrale.

### 🌟 Caractéristiques Principales

- 📸 Détection d'objets en temps réel
- 🔍 Recherche d'objets similaires
- 📊 Affichage des scores de confiance
- 💫 Interface utilisateur moderne et intuitive
- 📱 Design responsive (mobile, tablette, desktop)

## 🛠️ Technologies Utilisées

- **Backend**
    - Java Spring Boot
    - OpenCV (via JavaCV)
    - PostgreSQL avec pgvector
    - YOLOv4 pour la détection d'objets

- **Frontend**
    - HTML5 / CSS3
    - JavaScript (Vanilla)
    - Font Awesome
    - Google Fonts

## 📋 Prérequis

- Java JDK 17 ou supérieur
- Maven 3.8+
- PostgreSQL 14+ avec extension pgvector
- Git
- 4 Go de RAM minimum
- Connexion Internet stable

## 🚀 Installation

1. **Cloner le dépôt**

```bash
git clone https://github.com/yourusername/opencv-talk.git
cd opencv-talk
```

2. **Configurer PostgreSQL**

```sql
CREATE EXTENSION vector;
```

3**Installer les dépendances et compiler**

```bash
mvn clean install
```

5. **Lancer l'application**

```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse : `http://localhost:8080`

## 💡 Utilisation

1. Accédez à l'interface web
2. Glissez-déposez une image ou cliquez pour en sélectionner une
3. L'application analysera automatiquement l'image
4. Les résultats de détection s'afficheront avec les objets similaires

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créez votre branche (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add: Amazing Feature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📜 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👥 Équipe

- Chef de Projet : [Votre Nom]
- Développeurs :
    - [Membre 1]
    - [Membre 2]
- Design UI/UX : [Designer]

## 📞 Contact

- LinkedIn : [BANGA Romaric](https://www.linkedin.com/in/romaric-banga/)
- Twitter : [@bangaromaric](https://x.com/bangaromaric)

## 🙏 Remerciements

- [OpenCV](https://opencv.org/) pour leur bibliothèque de vision par ordinateur
- [YOLOv4](https://github.com/AlexeyAB/darknet) pour leur modèle de détection d'objets
- La communauté tech gabonaise pour leur soutien continu

## 📈 Feuille de Route

- [ ] Support de la détection en temps réel via webcam
- [ ] Interface en langues locales
- [ ] API REST publique
- [ ] Version mobile native
- [ ] Support hors-ligne
- [ ] Intégration avec d'autres services locaux

---
Fait avec ❤️ au Gabon 🇬🇦 