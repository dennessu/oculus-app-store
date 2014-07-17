#!/bin/bash
cd cart-integration
gradle clean build install -Pcart-integration -Dtest.env=local