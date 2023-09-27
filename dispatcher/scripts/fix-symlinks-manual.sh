echo FIXING SYMLINKS
# fix enabled_vhosts
cd ../src/conf.d/enabled_vhosts
rm ./default.vhost
ln -s ../available_vhosts/default.vhost
ls -l .
cd -

# fix enabled_farms
cd ../src/conf.dispatcher.d/enabled_farms
rm ./default.farm
ln -s ../available_farms/default.farm
ls -l .
cd -