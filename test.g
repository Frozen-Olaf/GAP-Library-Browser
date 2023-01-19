opt_list := OPERATIONS;
filt_list := FILTERS;

fname := Concatenation("operations", ".json");
fpath := Filename(DirectoryCurrent(), Concatenation("src/test/resources/dumps/", fname));

f := IO_File(fpath,"w");
IO_Write(f,"[");
size := Length(opt_list);
for elem in opt_list{[1..size-1]} do
    if not IsFilter(elem) then
        IO_Write(f, GapToJsonString(NameFunction(elem)));
        IO_Write(f, ",");
    fi;
od;

IO_Write(f,"]");
IO_Write(f, "\n");
IO_Flush(f);
IO_Close(f);


fname := Concatenation("filters", ".json");
fpath := Filename(DirectoryCurrent(), Concatenation("src/test/resources/dumps/", fname));

f := IO_File(fpath,"w");
IO_Write(f,"[");
for elem in filt_list do
    IO_Write(f, GapToJsonString(NameFunction(elem)));
    IO_Write(f, ",");
od;
IO_Write(f, GapToJsonString(NameFunction(IsObject)));
IO_Write(f,"]");
IO_Write(f, "\n");
IO_Flush(f);
IO_Close(f);
