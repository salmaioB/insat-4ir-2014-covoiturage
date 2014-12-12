-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           5.5.5-10.0.15-MariaDB - mariadb.org binary distribution
-- Serveur OS:                   Win64
-- HeidiSQL Version:             8.3.0.4694
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Export de la structure de la base pour covoitsopra
CREATE DATABASE IF NOT EXISTS `covoitsopra` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `covoitsopra`;


-- Export de la structure de table covoitsopra. city
CREATE TABLE IF NOT EXISTS `city` (
  `IdCity` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CityName` varchar(50) NOT NULL,
  `ZIPCode` varchar(50) NOT NULL,
  PRIMARY KEY (`IdCity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L'exportation de données n'été pas sélectionné.


-- Export de la structure de table covoitsopra. place
CREATE TABLE IF NOT EXISTS `place` (
  `IdPlace` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PlaceName` varchar(50) NOT NULL DEFAULT '0',
  `PlaceAddress` varchar(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IdPlace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L'exportation de données n'été pas sélectionné.


-- Export de la structure de table covoitsopra. route
CREATE TABLE IF NOT EXISTS `route` (
  `IdUser` int(10) unsigned NOT NULL,
  `IdCity` int(10) unsigned NOT NULL,
  `IdPlace` int(10) unsigned NOT NULL,
  `Hour` time NOT NULL,
  `Day` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  PRIMARY KEY (`IdUser`,`IdCity`,`IdPlace`),
  KEY `FK_route_city` (`IdCity`),
  KEY `FK_route_place` (`IdPlace`),
  KEY `IdUser` (`IdUser`),
  CONSTRAINT `FK_route_city` FOREIGN KEY (`IdCity`) REFERENCES `city` (`IdCity`),
  CONSTRAINT `FK_route_place` FOREIGN KEY (`IdPlace`) REFERENCES `place` (`IdPlace`),
  CONSTRAINT `FK_route_user` FOREIGN KEY (`IdUser`) REFERENCES `user` (`IdUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L'exportation de données n'été pas sélectionné.


-- Export de la structure de table covoitsopra. user
CREATE TABLE IF NOT EXISTS `user` (
  `IdUser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MailAddress` varchar(100) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `Password` varchar(50) NOT NULL,
  `Driver` enum('Y','N') NOT NULL,
  `IdCity` int(10) unsigned NOT NULL,
  `IdPlace` int(10) unsigned NOT NULL,
  PRIMARY KEY (`IdUser`),
  KEY `FK_user_city` (`IdCity`),
  KEY `FK_user_place` (`IdPlace`),
  CONSTRAINT `FK_user_city` FOREIGN KEY (`IdCity`) REFERENCES `city` (`IdCity`),
  CONSTRAINT `FK_user_place` FOREIGN KEY (`IdPlace`) REFERENCES `place` (`IdPlace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L'exportation de données n'été pas sélectionné.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
