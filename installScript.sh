# path to nbp_userdir
if [[ -z "$ADITODESIGNER_USERDIR" ]]; then
  echo "ADITODESIGNER_USERDIR is not set"
  exit 1
fi

NAME="de-adito-aditoweb-nbm-grouped-tabs"

cp "target/nbm/netbeans/extra/modules/$NAME.jar" "$ADITODESIGNER_USERDIR/modules/$NAME.jar"
cp -r "target/nbm/netbeans/extra/modules/ext/$NAME/" "$ADITODESIGNER_USERDIR/modules/ext/"
