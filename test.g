LoadPackage("io");
LoadPackage("json");

target_dir := Filename(DirectoryCurrent(), "src/test/resources/dumps/");
if IsExistingFile(target_dir) then
    opt_list := OPERATIONS;
    
    fname := Concatenation("operations", ".json");
    fpath := Concatenation(target_dir, fname);
    
    size := Length(opt_list);
    f := IO_File(fpath,"w");
    IO_Write(f, "[");
    for elem in opt_list{[1..size-1]} do
        if not IsFilter(elem) then
            IO_Write(f, GapToJsonString(NameFunction(elem)));
            IO_Write(f, ",");
        fi;
    od;
    IO_Write(f, "]");
    IO_Write(f, "\n");
    IO_Flush(f);
    IO_Close(f);
fi;
