import { initializeApp } from "https://www.gstatic.com/firebasejs/12.19.0/firebase-app.js";

const firebaseConfig = {
  apiKey: "AIzaSyBcdFbCzYMldwTohFLRmnFGGxB9ii7AMfc",
  authDomain: "aluguel-de-filmes.firebaseapp.com",
  projectId: "aluguel-de-filmes",
  storageBucket: "aluguel-de-filmes.firebasestorage.app",
  messagingSenderId: "405854616291",
  appId: "1:405854616291:web:4c0d9b0bb9e99b3f86e3d6"
};

export const app = initializeApp(firebaseConfig);
