
echo "Deploying to url" $1

echo "Copying jar"
scp -i ~/.ssh/theapproach.pem target/scala-2.12/approach-server-assembly-1.0.jar ec2-user@$1:server.jar

echo "Installing java 8"
ssh -i ~/.ssh/theapproach.pem -t ec2-user@$1 "yes | sudo yum install java-1.8.0"

echo "removing java 7"
ssh -i ~/.ssh/theapproach.pem -t ec2-user@$1 "yes | sudo yum remove java-1.7.0-openjdk"

