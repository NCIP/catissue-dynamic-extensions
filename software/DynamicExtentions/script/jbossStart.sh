echo ---------------------------------------------starting  jboss server ------------------------------------------------
echo cd $1/bin
cd $1/bin
export JBOSS_HOME=$1
run.sh -b 0.0.0.0 >DEserver.log
echo -------------------------------------NB part -2 finishes --------------------------------------------