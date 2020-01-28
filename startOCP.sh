if [ "$1" = "v3.9.0" ] 
then curl https://github.com/openshift/origin/releases/download/v3.9.0/openshift-origin-client-tools-v3.9.0-191fece-linux-64bit.tar.gz --location --output oc-client.tgz
elif [ "$1" = "v3.10.0" ]
then curl https://github.com/openshift/origin/releases/download/v3.10.0/openshift-origin-client-tools-v3.10.0-dd10d17-linux-64bit.tar.gz --location --output oc-client.tgz
elif [ "$1" = "v3.11.0" ]
then curl https://github.com/openshift/origin/releases/download/v3.11.0/openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit.tar.gz --location --output oc-client.tgz
fi
tar -xzvf oc-client.tgz
mv openshift-origin-client*/oc .
rm -rf openshift-origin-client*
./oc cluster up
