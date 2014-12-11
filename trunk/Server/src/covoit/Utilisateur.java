/******************************************************************************/
/* server/src/covoit/Utilisateur.java                              2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoiturage;


/** Compte d'un utilisateur, avec ses informations personnelles. */
public class Utilisateur
{
   public static Utilisateur load(String name)
   {
      return new Utilisateur();
      // puis select SQL, et copie des champs
   }
   
   public static Utilisateur create(String name, String hashedPassword)
   {
      return new Utilisateur();
      // puis insert SQL
   }

/******************************************************************************/

   private String name; // aussi l'adresse email ?
   private String passwd; // base64(bcrypt(<mot de passe>))
   private boolean driver; // Vrai si la personne préfère conduire elle-même.
   
   private Utilisateur() {}
}