curl https://github.com/openshift/origin/releases/download/$1/openshift-origin-client-tools-v3.10.0-dd10d17-linux-64bit.tar.gz --location --output oc-client.tgz
tar -xzvf oc-client.tgz
mv openshift-origin-client*/oc .
rm -rf openshift-origin-client*
./oc cluster up
