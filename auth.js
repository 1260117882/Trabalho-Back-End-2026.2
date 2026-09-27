import { getAuth } from "https://www.gstatic.com/firebasejs/12.19.0/firebase-auth.js";
import { app } from "./firebaseConfig.js";

export const auth = getAuth(app);
