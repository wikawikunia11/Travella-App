# PAP2025Z-Z13

# Opiekun
Mateusz Klimaszewski

# Zespół
1. Wiktoria Małażewska
2. Katarzyna Kuzora
3. Weronika Grzybowska
4. Gabryel Jundziłł

# Temat projektu
Aplikacja webowa w stylu social media do dzielenia się wrażeniami z podróży

# Mechanizm działania
1. Użytkownik zakłada konto na portalu.
2. Użytkownik loguje się na konto.
3. Przy dodaniu posta pobierana jest lokalizacja użytkownika (lub wybierana), wpisywana data, użytkownik wypełnia przygotowaną ankietę o miejscu pobytu.
4. Wyszukiwarka z filtrowaniem po lokalizacji.
5. W aplikacji widoczne są profile znajomych.
6. Użytkownik na profilu ma dostępną mapę z zaznaczonymi miejscami dodanych postów.
7. Możliwa jest personalizacja profilu, edytowanie jego w trakcie korzystania z portalu.

# Możliwe rozszerzenia
1. Dodawanie zdjęć do postów
2. Statusy relacji znajomości - chęć bycia znajomymi musi być zatwierdzona przez obie strony.
3. Rozwinięcie opisu postu na szczegółowe pola, np. czynności, pogoda, osoby towarzyszące.

# Polecenia potrzebne, żeby zbudować kontenery od początku (lub pobrać dodane zależności)
```bash
docker compose up -d --build
```

# Żeby uruchomić bazę danych od nowa (wywołać seed.sql)
```bash
docker-compose down -v
docker-compose up
```
