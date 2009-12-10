cd ../temp/project/software/DynamicExtentions

echo ===============================================================================

call ant create_dynamic_extensions_zip

call ant create_dyn_ext_interface_jar

call ant create_category_creator_zip

echo ===============================================================================