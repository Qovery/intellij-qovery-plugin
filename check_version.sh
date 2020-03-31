curl https://raw.githubusercontent.com/Qovery/intellij-qovery-plugin/master/build.gradle > master_gradle

ver_master=$(cat master_gradle | grep "version '" | sed -n 2p)
ver_current=$(cat build.gradle | grep "version '" | sed -n 2p)

echo "current version: $ver_current; master version: $ver_master"

if test "$ver_master" = "$ver_current"; then
  exit 1
else
  exit 0
fi
