import { getFirestore } from "https://www.gstatic.com/firebasejs/12.19.0/firebase-firestore.js";
import { app } from "./firebaseConfig.js";

export const db = getFirestore(app);
