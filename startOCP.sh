curl https://mirror.openshift.com/pub/openshift-v3/$1/linux/oc.tar.gz --location --output oc.tar.gz
gunzip oc.tar.gz
tar -xvf oc.tar
./oc cluster up
