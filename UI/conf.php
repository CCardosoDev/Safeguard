<?php

/*
  $deploy = FALSE;

  if ($deploy) {
  $geoRefLink = "";
  $calendarLink = "";
  $authLink = "";
  $profilesLink = "";
  $compositionLink = "";
  } else {
  $geoRefLink = "https://192.168.8.217:7706";
  $calendarLink = "https://192.168.8.217:7710";
  $authLink = "192.168.8.217:7704";
  $profilesLink = "https://192.168.8.217:7706";
  $compositionLink = "https://192.168.8.217:7706";
  }
 */
/* array(
  "geoRefLink" => $geoRefLink,
  "calendarLink" => $calendarLink,
  "authLink" => $authLink,
  "profilesLink" => $profilesLink,
  "compositionLink" => $compositionLink,
  );
 */

function geoRefLink() {
    if (getenv("safeguardState") == "production") {
        return "https://192.168.215.216:8181";
    }

    return "https://192.168.8.217:7706";
}

function authLink() {
    if (getenv("safeguardState") == "production") {
        return "192.168.215.216:5000";
    }
    return "192.168.8.217:7704";
}

function homeLink() {
    if (getenv("safeguardState") == "production") {
        return "192.168.8.217:7701";
    }
    return "localhost";
}

function calendarLink() {
    if (getenv("safeguardState") == "production") {
        return "https://192.168.215.216:5001";
    }
    return "https://192.168.8.217:7710";
}

function redirectLink() {
    if (getenv("safeguardState") == "production") {
        return "192.168.8.217:7701";
    }
    return "localhost"; //"192.168.8.217:7701";
}

function redirectAuthLink() {
    if (getenv("safeguardState") == "production") {
        return "192.168.8.217:7704";
    }
    return "192.168.8.217:7704"; //"192.168.8.217:7701";
}
