#!/bin/bash

# please put the file under the batch directory: ${payment.jobs.batch.batchDirectory}

file_name="index.txt"
if [ ! -f $file_name ] 
then
	echo "index file $file_name not exists! please add one first with index."
	exit
fi

index=`cat $file_name`
echo $index

batch_prefix="settlement_detail_report_batch_"
batch_format=".csv"

while(true)
do
download_index=$(($index + 1))
batch_name=$batch_prefix$download_index$batch_format
echo "download file: $batch_name"

curl -O -u report@Company.Oculus:passw0rdpasswordpassw0rd https://ca-test.adyen.com/reports/download/MerchantAccount/OculusCOM/$batch_name

if [ -f $batch_name ] 
then
	echo "download $batch_name successfully. update index:"
	index=$download_index
	echo "new index is: $index;"
	echo $download_index > $file_name
else
	echo "download $batch_name failed."
	break
fi
done


