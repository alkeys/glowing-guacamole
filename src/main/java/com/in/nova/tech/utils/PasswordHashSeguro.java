package com.in.nova.tech.utils;

import org.mindrot.jbcrypt.BCrypt;

public  class PasswordHashSeguro {

    /***
     * Hashea la contrasena de forma segura usando Bcrypt
     * @param password La contrasena en texto plano
     * @return El hash seguro que incluye el salt
     */
    public static String hashPassword(String password) {
        // Bcrypt se encarga de todo: genera un salt aleatorio y hashea
        // El resultado ya es el hash listo para guardar en la base de datos
        // con el salt incluido.
        if (password != null && !password.isEmpty()) {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        }
        return null;
    }

    /***
     * Metodo para verificar una contrasena
     * @param password La contrasena en texto plano
     * @param hashed La contrasena hasheada que esta en la base de datos
     * @return true si coinciden, false si no
     */
    public static boolean checkPassword(String password, String hashed) {
        // Bcrypt compara el hash de forma segura
        return BCrypt.checkpw(password, hashed);
    }
}