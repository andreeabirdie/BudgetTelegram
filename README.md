# BudgetTelegram
exam model messaging app
Messages

Pentru vizualizarea mesajelor, creati un sistem client-server dupa cum urmeaza.
Serverul expune prin http (localhost:3000) un API REST peste resursa Message.
Un mesaj are un id - numar intreg, un text - sir de caractere,
un indicator read - indicand daca a fost citit sau nu, sender - numele
utilizatorului care a trimis mesajul si  created - data la care a fost creat (numar intreg).
Dezvoltati o aplicatie mobila (client) dupa cum urmeaza.

1. La pornirea aplicatiei, se descarca lista mesajelor de pe server, via
http GET /message. Aplicatia afiseaza lista utilizatorilor care au trimis mesaje.
sta
2. Daca exista mesaje necitite primite de la anumiti utilizatori, acestia vor fi
prezentati primii in lista, ordonati descrecator dupa data ulimului mesaj necitit.

3. Pentru fiecare utilizator aplicatia prezinta numarul mesajelor necitite in formatul user [unread count].

4. La un click pe un utilizator, aplicatia prezinta lista mesajele primite de la acel utilizator
(in acelasi ecran). Mesajele sunt sortate in lista crescator dupa created.

5. Un mesaj necitit si vizibil in lista este aratat timp de o secunda in bold, pentru a indica
vizual faptul ca e mesaj nou, pe care utilizatorul tocmai il citeste.


6. Navigand printre mesaje, la fiecare mesaj necitit si afisat in lista de mesaje, aplicatia transmite
catre server faptul ca utilizatorul a citit mesajelul respectiv via PUT /message/:id.
De asemenea, lista utilizatorilor va fi actualizata, prezentand numarul mesajelor necitite
pentru utilizatorul selectat.

7. Aplicatia primeste notificari de la server privind mesajele noi primite (via ws).

8. Aplicatia persista local mesajele si starea mesajelor citite.

9. Daca device-ul nu se poate conecta la server la pasul (6), in mod automat, aplicatia
va reincerca transmiterea catre server a starii citirii mesajelor.
