#!/bin/bash

function digshort {
  dig +short $1 | sort
}

API_OCULUS_RESULT=$(digshort api.oculus.com)
API_PREPROD_OCULUS_RESULT=$(digshort api-preprod.oculus.com)

UW_BLUE_RESULT=$(digshort dualstack.sewer-pub-prod-blue-us-west-69012490.us-west-1.elb.amazonaws.com)
UW_GREEN_RESULT=$(digshort dualstack.sewer-pub-prod-green-us-west-1382431556.us-west-1.elb.amazonaws.com)

UE_BLUE_RESULT=$(digshort dualstack.sewer-pub-prod-blue-us-east-1781004205.us-east-1.elb.amazonaws.com)
UE_GREEN_RESULT=$(digshort dualstack.sewer-pub-prod-green-us-east-2119453765.us-east-1.elb.amazonaws.com)

if [[ $API_OCULUS_RESULT = $UW_BLUE_RESULT ]] || [[ $API_OCULUS_RESULT = $UE_BLUE_RESULT ]]; then
  echo "api.oculus.com is pointing to: BLUE"
elif [[ $API_OCULUS_RESULT = $UW_GREEN_RESULT ]] || [[ $API_OCULUS_RESULT = $UE_GREEN_RESULT ]]; then
  echo "api.oculus.com is pointing to: GREEN"
else
  echo "api.oculus.com is pointing to: UNKNOWN"
fi

if [[ $API_PREPROD_OCULUS_RESULT = $UW_BLUE_RESULT ]] || [[ $API_PREPROD_OCULUS_RESULT = $UE_BLUE_RESULT ]]; then
  echo "api-preprod.oculus.com is pointing to: BLUE"
elif [[ $API_PREPROD_OCULUS_RESULT = $UW_GREEN_RESULT ]] || [[ $API_PREPROD_OCULUS_RESULT = $UE_GREEN_RESULT ]]; then
  echo "api-preprod.oculus.com is pointing to: GREEN"
else
  echo "api-preprod.oculus.com is pointing to: UNKNOWN"
fi
