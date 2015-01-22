-- --------------------------------------------------------
-- Hôte:                         remspi.noip.me
-- Version du serveur:           5.5.37-0+wheezy1 - (Debian)
-- Serveur OS:                   debian-linux-gnu
-- HeidiSQL Version:             9.1.0.4867
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Export de la structure de la base pour covoit
CREATE DATABASE IF NOT EXISTS `covoit` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `covoit`;


-- Export de la structure de table covoit. admin
CREATE TABLE IF NOT EXISTS `admin` (
  `IdAdmin` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `LastName` varchar(50) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `Login` varchar(50) NOT NULL,
  `Password` varchar(60) NOT NULL,
  PRIMARY KEY (`IdAdmin`),
  UNIQUE KEY `Login` (`Login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L'exportation de données n'été pas sélectionné.


-- Export de la structure de table covoit. city
CREATE TABLE IF NOT EXISTS `city` (
  `IdCity` int(11) NOT NULL AUTO_INCREMENT,
  `CityName` text NOT NULL,
  `ZipCode` varchar(10) NOT NULL,
  PRIMARY KEY (`IdCity`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L'exportation de données n'été pas sélectionné.


-- Export de la structure de table covoit. place
CREATE TABLE IF NOT EXISTS `place` (
  `IdPlace` int(11) NOT NULL AUTO_INCREMENT,
  `PlaceName` text NOT NULL,
  `PlaceAddress` text NOT NULL,
  PRIMARY KEY (`IdPlace`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L'exportation de données n'été pas sélectionné.


-- Export de la structure de table covoit. route
CREATE TABLE IF NOT EXISTS `route` (
  `IdUser` int(11) NOT NULL,
  `Day` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `GoHour` time NOT NULL,
  `ReturnHour` time NOT NULL,
  `IdPlace` int(11) NOT NULL,
  `Notify` bit(1) DEFAULT b'1',
  PRIMARY KEY (`IdUser`,`Day`),
  KEY `IdPlace` (`IdPlace`),
  CONSTRAINT `IdPlace` FOREIGN KEY (`IdPlace`) REFERENCES `place` (`IdPlace`),
  CONSTRAINT `IdUser` FOREIGN KEY (`IdUser`) REFERENCES `user` (`IdUser`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L'exportation de données n'été pas sélectionné.


-- Export de la structure de table covoit. user
CREATE TABLE IF NOT EXISTS `user` (
  `IdUser` int(11) NOT NULL AUTO_INCREMENT,
  `MailAddress` varchar(100) NOT NULL,
  `LastName` varchar(100) NOT NULL,
  `FirstName` varchar(100) NOT NULL,
  `Password` varchar(60) NOT NULL,
  `Driver` char(1) NOT NULL DEFAULT 'N',
  `IdCity` int(11),
  PRIMARY KEY (`IdUser`),
  UNIQUE KEY `MailAddress` (`MailAddress`),
  KEY `IdCity` (`IdCity`),
  CONSTRAINT `IdCity` FOREIGN KEY (`IdCity`) REFERENCES `city` (`IdCity`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- L'exportation de données n'été pas sélectionné.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
