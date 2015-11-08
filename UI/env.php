<?php

    if (getenv("safeguardState") == "production") {
        echo "production";
    }

    echo "test";
?>