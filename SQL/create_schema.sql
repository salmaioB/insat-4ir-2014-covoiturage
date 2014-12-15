-- --------------------------------------------------------
-- Host:                         felix-host.ddns.net
-- Server version:               5.5.5-10.0.15-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             8.3.0.4694
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for covoitsopra
DROP DATABASE IF EXISTS `covoitsopra`;
CREATE DATABASE IF NOT EXISTS `covoitsopra` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `covoitsopra`;


-- Dumping structure for table covoitsopra.admin
DROP TABLE IF EXISTS `admin`;
CREATE TABLE IF NOT EXISTS `admin` (
  `IdAdmin` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `LastName` varchar(50) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `Login` varchar(50) NOT NULL,
  `Password` varchar(60) NOT NULL,
  PRIMARY KEY (`IdAdmin`),
  UNIQUE KEY `Login` (`Login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table covoitsopra.city
DROP TABLE IF EXISTS `city`;
CREATE TABLE IF NOT EXISTS `city` (
  `IdCity` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CityName` varchar(50) NOT NULL,
  `ZIPCode` varchar(50) NOT NULL,
  PRIMARY KEY (`IdCity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table covoitsopra.place
DROP TABLE IF EXISTS `place`;
CREATE TABLE IF NOT EXISTS `place` (
  `IdPlace` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PlaceName` varchar(50) NOT NULL DEFAULT '0',
  `PlaceAddress` varchar(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IdPlace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table covoitsopra.route
DROP TABLE IF EXISTS `route`;
CREATE TABLE IF NOT EXISTS `route` (
  `IdUser` int(10) unsigned NOT NULL,
  `Day` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `GoHour` time DEFAULT NULL,
  `ReturnHour` time DEFAULT NULL,
  PRIMARY KEY (`IdUser`,`Day`),
  CONSTRAINT `FK_route_user` FOREIGN KEY (`IdUser`) REFERENCES `user` (`IdUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table covoitsopra.user
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `IdUser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MailAddress` varchar(100) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `Password` varchar(60) NOT NULL,
  `Driver` enum('Y','N') NOT NULL,
  `IdCity` int(10) unsigned DEFAULT NULL,
  `IdPlace` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`IdUser`),
  UNIQUE KEY `MailAddress` (`MailAddress`),
  KEY `FK_user_city` (`IdCity`),
  KEY `FK_user_place` (`IdPlace`),
  CONSTRAINT `FK_user_city` FOREIGN KEY (`IdCity`) REFERENCES `city` (`IdCity`),
  CONSTRAINT `FK_user_place` FOREIGN KEY (`IdPlace`) REFERENCES `place` (`IdPlace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
