# 🗺️ Route My Way — Android App for Urban Travelers

Aplikacja mobilna na system Android, która umożliwia szybkie i wygodne planowanie optymalnej trasy zwiedzania w dużych miastach na podstawie punktów wybranych przez użytkownika.

---
<img width="302" height="776" alt="image" src="https://github.com/user-attachments/assets/1d92c54e-aa6c-4d59-a589-3a773feae691" />
<img width="302" height="776" alt="image" src="https://github.com/user-attachments/assets/e69cbb8e-7bdc-4c15-ae94-aaa4249df7a0" />
<img width="302" height="776" alt="image" src="https://github.com/user-attachments/assets/c06815dd-2c7b-4fe2-aeb5-8dd8730364c7" />
<img width="602" height="776" alt="image" src="https://github.com/user-attachments/assets/7fc119b6-8788-48db-8495-3f300e26f69c" />

<img width="302" height="776" alt="image" src="https://github.com/user-attachments/assets/fcb05966-f056-4ab1-ac4d-4c18e7688fa7" />

<img width="302" height="776" alt="image" src="https://github.com/user-attachments/assets/29b48cfe-b169-4854-be2f-bec4214d86db" />
<img width="302" height="776" alt="image" src="https://github.com/user-attachments/assets/af09dc5b-15e8-4f7e-a14f-28373bc4e9c1" />
---

## 📱 Opis

**CityTour Planner** pozwala użytkownikowi wyznaczyć trasę zwiedzania w korzystając z algorytmu Quasi Optymalnego, dzięki któremu można wyznaczyć pseudooptymalną trasę nawet przy większej ilości punktów w szybkim czasie.

Punkty trasy można wskazać:
- klikając bezpośrednio na mapie,
- lub wyszukując miejsca przy pomocy wbudowanej wyszukiwarki miejsc Google

Aplikacja oparta jest na Google Maps i wykorzystuje algorytm wyznaczania trasy zbliżony do problemu komiwojażera (TSP). 
Dla wygody użytkownika możliwe jest otwarcie zaplanowanej trasy w aplikacji Google Maps z aktywną nawigacją.

Aplikacja rozwiązuje problem braku funkcji optymalizacji trasy w mapach Google Maps

---

## 🧠 Główne funkcjonalności

- ✅ Interaktywny widok mapy (Google Maps SDK)
- ✅ Wybieranie punktów trasy: kliknięcie na mapie lub wyszukiwarka
- ✅ Wyznaczanie quasi-optymalnej trasy zwiedzania
- ✅ Integracja z Google Maps w celu nawigacji
- ✅ Obsługa rzeczywistych danych o miejscach i trasach

---

## 🌐 Wykorzystywane API

- [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk)
- [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview)
- [Google Directions API](https://developers.google.com/maps/documentation/directions)

---

## ⚙️ Technologie

- Android SDK
- Java
- Google Play Services

---

## 🧭 Przykładowy scenariusz użycia

1. Użytkownik otwiera aplikację i widzi mapę swojego miasta.
2. Wybiera punkty do odwiedzenia — klikając na mapie lub wyszukując konkretne miejsca.
3. Aplikacja oblicza quasi-optymalną trasę zwiedzania.
4. Użytkownik może przejrzeć trasę wizualnie lub kliknąć „Nawiguj”, by otworzyć ją w Google Maps.

---

## 🚀 Uruchamianie projektu

1. Wymagane klucze API Google (Maps, Directions, Places) — dodaj do `local.properties` lub `gradle.properties`.
2. Skonfiguruj projekt w Android Studio.
3. Uruchom na emulatorze lub urządzeniu fizycznym z dostępem do internetu.

---

