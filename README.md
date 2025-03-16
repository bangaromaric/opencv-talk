# 🤖 AI Vision - Détection et Classification d'Objets au Gabon

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Made in Gabon](https://img.shields.io/badge/Made%20in-Gabon-green?style=flat&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAzIDIiPjxwYXRoIGZpbGw9IiMwMDk4M2EiIGQ9Ik0wIDBoM3YySDB6Ii8+PHBhdGggZmlsbD0iI2ZmYzQwMCIgZD0iTTAgLjY2N2gzdjAuNjY2SDB6Ii8+PHBhdGggZmlsbD0iIzAwMDA5OCIgZD0iTTAgMS4zMzNoM3YwLjY2NkgweiIvPjwvc3ZnPg==)](https://github.com/yourusername/opencv-talk)
[![Open Source](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://opensource.org/)

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

## 🔍 Dépannage

### Problèmes avec les modèles YOLOv4

1. **Vérification de l'installation**

```bash
# Vérifier la présence des fichiers
ls -l src/main/resources/models/yolo/
```

Vous devriez voir les fichiers suivants :

```
yolov4.cfg
yolov4.weights
coco.names
```

2. **Tailles attendues des fichiers**

- `yolov4.cfg` : environ 13 KB
- `yolov4.weights` : environ 250 MB
- `coco.names` : environ 1 KB

3. **Problèmes courants**

- **Erreur "No such file or directory"** :
  ```
  Cause : Les modèles ne sont pas au bon endroit
  Solution : Vérifier la structure des dossiers et les droits d'accès
  ```

- **Erreur "Failed to load darknet model"** :
  ```
  Cause : Fichiers corrompus ou incomplets
  Solution : Retélécharger les fichiers
  ```

- **Erreur "CUDA out of memory"** :
  ```
  Cause : RAM GPU insuffisante
  Solution : Passer en mode CPU dans application.properties :
  opencv.preferableBackend=0
  opencv.preferableTarget=0
  ```

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

3. **Télécharger les modèles YOLOv4**

```bash
# Créer le dossier des modèles
mkdir -p src/main/resources/models/yolo

# Télécharger les fichiers de configuration et de poids YOLOv4
cd src/main/resources/models/yolo

# Télécharger le fichier de configuration
wget https://raw.githubusercontent.com/AlexeyAB/darknet/master/cfg/yolov4.cfg

# Télécharger les poids pré-entraînés (environ 250MB)
wget https://github.com/AlexeyAB/darknet/releases/download/darknet_yolo_v3_optimal/yolov4.weights

# Télécharger les noms des classes
wget https://raw.githubusercontent.com/AlexeyAB/darknet/master/data/coco.names

# Retourner au dossier racine
cd ../../../../
```

> **Note**: Si `wget` n'est pas installé, vous pouvez télécharger manuellement les fichiers aux URLs suivantes :
> - Configuration : https://raw.githubusercontent.com/AlexeyAB/darknet/master/cfg/yolov4.cfg
> - Poids : https://github.com/AlexeyAB/darknet/releases/download/darknet_yolo_v3_optimal/yolov4.weights
> - Classes : https://raw.githubusercontent.com/AlexeyAB/darknet/master/data/coco.names
>
> Placez ensuite les fichiers dans le dossier `src/main/resources/models/yolo/`

4. **Installer les dépendances et compiler**

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

## 📜 Licence et Open Source

Ce projet est open source et distribué sous la licence Apache 2.0. Cela signifie que vous pouvez librement :

### Ce que vous pouvez faire :

- ✅ Utiliser le code à des fins commerciales
- ✅ Modifier le code source
- ✅ Distribuer le code modifié
- ✅ Utiliser le code dans des projets privés
- ✅ Utiliser le code dans des projets commerciaux

### Vos obligations :

- 📝 Inclure une copie de la licence
- 📝 Indiquer clairement les modifications apportées
- 📝 Conserver les mentions de droits d'auteur
- 📝 Inclure une notice indiquant l'utilisation de ce code

### Comment citer ce projet :

```bibtex
@software{ai_vision_gabon,
  author = {BANGA, Romaric},
  title = {AI Vision - Détection et Classification d'Objets},
  year = {2024},
  publisher = {GitHub},
  url = {https://github.com/yourusername/opencv-talk}
}
```

### Contribution à l'Open Source

Nous encourageons activement la communauté tech africaine à contribuer à ce projet. Voici comment vous pouvez
participer :

- 🐛 Signaler des bugs
- 💡 Proposer des améliorations
- 📖 Améliorer la documentation
- 🌍 Ajouter des traductions
- 🔧 Soumettre des corrections

Pour plus de détails, consultez le fichier [LICENSE](LICENSE) et [CONTRIBUTING.md](CONTRIBUTING.md).

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