LoadPackage("io");
LoadPackage("json");

FiltersOfAllArguments := function(methods, i, t)
    local j, k, f, res, temp, flags, fvalues;
    res := [];
    for j in [0..i] do
        temp := [];
        flags := methods[2+j+t];
        fvalues := TRUES_FLAGS(WITH_IMPS_FLAGS(flags));
        for k in fvalues do
            f := FILTERS[k];
            if IsFilter(f) then
                Add(temp, NAME_FUNC(f));
            fi;
        od;
        Add(temp, NAME_FUNC(IsObject));
        Add(res, temp);
    od;
    return res;
end;

AllMethodsFromOperation := function(opt)
    local i, j, d, t, res, methods, method_num, filters, rank, name, property, src_info, src, mthd_rec;
    res := [];
    for i in [0..5] do
        methods := METHODS_OPERATION(opt, i+1);
        d := 7+i;
        method_num := Length(methods)/d;
      
        for j in [0..(method_num-1)] do
            t := d*j;
            filters := FiltersOfAllArguments(methods, i, t);
            rank := methods[4+i+t];
            if not IsInt(rank) then
                rank:=String(rank);
            fi;
            property := rec(filters:=filters, rank:=rank);
            name := methods[5+i+t];
            src_info := methods[6+i+t];
            src := rec(file_path:=src_info[1], line_num_start:=src_info[2], line_num_end:=src_info[3]);
            mthd_rec := rec(mthd_name:=name, property:=property, src:=src);
            Add(res, mthd_rec);
        od;
    od;
    return res;
end;

opt_rec_list := [];
opt_list := OPERATIONS;

for opt in opt_list do
    name := NAME_FUNC(opt);
    if IsAttribute(opt) then
        opt_rec := rec(atr_name:=name);
    elif IsProperty(opt) then
        opt_rec := rec(prp_name:=name);
    elif IsFilter(opt) then
        continue;
    elif IsOperation(opt) then
        opt_rec := rec(opt_name:=name);
    fi;
    opt_rec.methods := AllMethodsFromOperation(opt);
    Add(opt_rec_list, opt_rec);
od;

date := Filename(DirectoriesSystemPrograms(), "date");
time_str := "";
tmp := OutputTextString(time_str,true);
Process(DirectoryCurrent(), date, InputTextNone(), tmp, []);
CloseStream(tmp);
NormalizeWhitespace(time_str);
time_list := SplitString(time_str, " ");
datetime_list := time_list{[2,3,4,5]};
datetime := JoinStringsWithSeparator(datetime_list,"-");
datetime := ReplacedString(datetime,":","-");

fname := Concatenation("dump-", Concatenation(datetime, ".json"));
# fpath := Filename(DirectoriesLibrary("")[1], Concatenation("dump/", fname));
fpath := Filename(Directory(IO_getcwd()), fname);

f := IO_File(fpath,"w");
IO_Write(f, "GAP root directory: ",  Last(GAPInfo.RootPaths));
IO_Write(f, "\n");
IO_Write(f, GapToJsonString(opt_rec_list[1]));
for elem in opt_rec_list{[2..Length(opt_rec_list)]} do
    IO_Write(f, ",\n");
    IO_Write(f, GapToJsonString(elem));
od;
IO_Write(f, "\n");
IO_Flush(f);
IO_Close(f);


# Test Module
if IsExistingFile("test.g") then
    if IsReadableFile("test.g") then
        Read("test.g");
    else
        Print(LastSystemError().message);
    fi;
fi;
