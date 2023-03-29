package model.data;

public abstract class ModelDataValidation {

    protected static final int MINIMUM_OPERATION_NUMBER = 3000;
    protected static final int MINIMUM_FILTER_NUMBER = 2000;
    protected static final int MINIMUM_METHOD_NUMBER = 3000;

    protected static final String[] FUNDAMENTAL_OPERATION_NAMES = {"Order", "Size", "Intersection2", "Union2", "Matrix", "Vector", "PermutationCycleOp",
            "FieldOfMatrixGroup", "PolynomialRing", "Subrings", "StandardGeneratorsSubringSCRing", "Basis", "BasisVectors",
            "Sin", "Sinh", "Cos", "Cosh", "Tan", "Tanh", "Cot", "Coth", "SetOrder", "SetSize", "String", "Character", "Int", "ListOp", "+", "-", "*", "/",
            "<", "=", "^", "[]:=", "{}:="
    };

    protected static final String[] FUNDAMENTAL_METHOD_NAMES = {"Order", "Order: system getter", "Order: object with memory",
            "Order: ordinary matrix of finite field elements", "Order: generic method for ordinary matrices", "Order: for a group", 
            "Order: for an internal FFE", "Order: for a general FFE", "Order: for element in Z/nZ (ModulusRep)", "Order: for a permutation",
            "Order: for a transformation", "Order: free group element", "Order: for a mult. element-with-one", 
            "Size", "Size: system getter", "Size: general linear group", "Size: for groups of FFE", "Size: for a permutation group that knows to be a direct product", 
            "Size: for a permutation group", "Size: for a free group", "Size: for a matrix group that knows to be a direct product", "Size: for a cyclic group", 
            "Size: group direct product", "Size: for a free semigroup", "Size: for a list", "Size: for a collection",
            "Union2: for two collections that are lists", "Union2: for two collections", "Union2: for class and class/list/collection",
            "Intersection2", "Intersection2: perm cosets", "Intersection2: perm groups", "Intersection2: for two fields of FFEs", "Intersection2: for two abelian number fields",
            "Intersection2: method for two vector spaces", "Intersection2: for two collections in the same family",
            "Matrix", "Matrix: for a list and a zmodnz matrix", "Vector", "Vector: for a list and a zmodnz vector", "Vector: for a list of gf2 elements and a gf2 vector", 
            "PermutationCycleOp: of object in list", "PermutationOp: object on list", "FieldOfMatrixGroup: for a matrix group",
            "PolynomialRing", "Subrings: for SC Rings", "StandardGeneratorsSubringSCRing: system getter",
            "Basis: system getter", "Basis: for a finite field (delegate to `CanonicalBasis')", "Basis: for a finite field, and a hom. list",
            "BasisVectors: system getter", "BasisVectors: for a mutable basis of a Gaussian matrix space",
            "BasisVectors: for canonical basis of a full matrix module", "BasisVectors: for mutable basis represented by an immutable basis",
            "Sin: system getter", "Sin: for macfloats", "Sinh: system getter", "Sinh: for macfloats",
            "Cos: system getter", "Cos: for macfloats", "Cosh: system getter", "Cosh: for macfloats",
            "Tan: system getter", "Tan: for macfloats", "Tanh: system getter", "Tanh: for macfloats",
            "Cot: system getter", "Cot: for floats", "Coth: system getter", "Coth: for floats",
            "SetOrder: system setter", "SetSize",
            "String: system getter", "String: for a field of FFEs", "String: for natural symmetric group", "String: for a polynomial ring", "String: for a group",
            "String: for zmodnz matrix", "String: for element in Z/pZ (ModulusRep)", "String: for infinity", "String: for a permutation", "String: for a transformation",
            "String: for a boolean", "String: for a character", "Character: for a group, and a dense list", "Int: system getter", "Int: for an integer", "Int: for element in Z/nZ (ModulusRep)",
            "ListOp: for a zmodnz matrix", "ListOp: for a plist matrix", "ListOp: for a list", "ListOp: for a collection", "ListOp: for an iterator",
            "+", "-", "*", "/", "<", "=", "^", "[]:=", "{}:=: for a mutable list, a dense list, and a list"
    };
    
    protected static final String[] FUNDAMENTAL_METHOD_FILE_PATHS = {"./lib/ffeconway.gi", "./lib/arith.gd", "./lib/memory.gi", "./lib/matrix.gi", "./lib/grp.gi", "./lib/ffe.gi", 
            "./lib/zmodnz.gi", "./lib/ffeconway.gi", "./lib/permutat.g", "./lib/trans.gi", "./lib/grpfree.gi", "./lib/arith.gi", "./lib/coll.gi", "./lib/coll.gd",
            "./lib/grpffmat.gi", "./lib/fieldfin.gi", "./lib/gprdperm.gi", "./lib/grpperm.gi", "./lib/gprdmat.gi", "./lib/gprd.gi", "./lib/fpsemi.gi", "./lib/smgrpfre.gi",
            "./lib/list.gi", "./lib/gpprmsya.gi", "./lib/csetperm.gi", "./lib/stbcbckt.gi", "./lib/float.gd", "./lib/ieee754.g", "./lib/object.gd", "./lib/ringpoly.gi",
            "./lib/gpprmsya.gi", "./lib/matobjnz.gi", "./lib/zmodnz.gi", "./lib/ctblfuns.gi", "./lib/cyclotom.g", "./lib/reesmat.gi", "./lib/matobj.gi", "./lib/matobjnz.gi",
            "./lib/vecmat.gi", "./lib/oprt.gi", "./lib/ringsc.gi", "./lib/ringsc.gd", "./lib/basis.gd", "./lib/fieldfin.gi", "./lib/vspcmat.gi", "./lib/modulmat.gi", "./lib/basismut.gi",
            "./lib/matobjplist.gi"
    };
    
    protected static final String[] FUNDAMENTAL_FILTERS = {"IsList", "IsRDistributive", "HasIsGeneratorsOfMagmaWithInverses", "IsAssociativeLOpDProd", "HasString", "HasIsWholeFamily",
            "CategoryCollections(CategoryCollections(CategoryCollections(IsNearAdditiveElementWithZero)))", "HasIsDuplicateFree", "HasIsAdditivelyCommutative",
            "CategoryCollections(CategoryCollections(CategoryCollections(IsMultiplicativeElement)))", "IsLDistributive", "IsPcpElement", "IsTransformation", "IsSubgroupFpGroup",
            "CategoryCollections(IsSCRingObj)", "CanEasilyCompareElements", "IsObject", "CanEasilySortElements", "IsExternalSet", "CategoryCollections((IsWord and IsAssociativeElement))",
            "IsMutableBasisOfGaussianMatrixSpaceRep", "IsSubgroupFgGroup", "CategoryCollections(IsAdditiveElement)", "IsRat", "IsMonoidAsSemigroup", "HasTanh", "IsUniqueFactorizationRing",
            "IsDataObjectRep", "CanComputeSize", "IsInfinity", "HasIsGeneratorsOfSemigroup", "CanComputeSizeAnySubgroup", "CanComputeFittingFree", "IsBool", "IsPerm", "IsRightCoset",
            "CategoryCollections(IsNearAdditiveElementWithZero)", "CanEasilyTestMembership", "HasIsRegularSemigroup", "IsLogOrderedFFE", "CategoryCollections(IsWord)", "HasIsInverseSemigroup",
            "IsWholeFamily", "IsExtLSet", "HasIsLDistributive", "IsAdditivelyCommutative", "HasIsCommutative", "IsHomogeneousList", "IsCanonicalBasisFullMatrixModule", "IsPolynomialRing",
            "IsIEEE754FloatRep", "IsOrthodoxSemigroup", "CategoryCollections(IsAssociativeElement)", "IsFinitelyGeneratedMagma", "CategoryCollections(IsZmodnZepsObj)", "IsZDFRE",
            "IsDistributiveLOpESum", "IsAdditiveElement", "IsInt", "HasIsFinitelyGeneratedGroup", "CategoryCollections(IsNearAdditiveElement)", "IsNearAdditiveElementWithInverse",
            "IsMultiplicativeElementWithInverse", "IsFinite", "HasCanEasilySortElements", "IsMatrixOrMatrixObj", "HasMultiplicativeNeutralElement", "IsFunction", "CategoryCollections(IsMultiplicativeElement)",
            "HasZeroOfBaseDomain", "CategoryCollections(IsExtRElement)", "IsBasis", "IsExternalOrbit", "CategoryCollections(IsZDFRE)", "IsFinitelyGeneratedMonoid", "IsGeneratorsOfSemigroup",
            "CategoryCollections(IsReesZeroMatrixSemigroupElement)", "HasIsCompletelySimpleSemigroup", "HasTan", "IsSimpleSemigroup", "IsSubsetLocallyFiniteGroup",
            "CategoryCollections(CategoryCollections(CategoryCollections(IsNearAdditiveElement)))", "IsMagma", "HasIsTorsionFree", "IsExtLElement", "IsExtRElement", "IsRealFloat", "IsMatrixObj",
            "IsGeneralizedDomain", "IsEuclideanRing", "IsFloat", "HasIsMonoidAsSemigroup", "HasCos", "IsChar", "HasCot", "IsCommutative", "CategoryCollections(IsExtLElement)", "IsCyclotomic",
            "HasIsFinitelyGeneratedMonoid", "IsGF2VectorRep", "CategoryCollections(((IsWord and IsAssociativeElement) and (IsWord and IsMultiplicativeElementWithOne)))", "IsModulusRep", "HasIsSmallList",
            "IsTorsionFree", "IsCopyable", "IsVectorObj", "IsFiniteOrderElement", "IsCollection", "HasKnowsHowToDecompose", "IsNonTrivial", "CategoryCollections(CategoryCollections(CategoryCollections(IsAdditiveElement)))",
            "HasSinh", "HasIsCanonicalBasis", "IsListOrCollection", "CategoryCollections(IsCommutativeElement)", "IsNearAdditiveMagmaWithZero", "IsNaturalSymmetricGroup", "IsCanonicalBasis", "IsCanonicalBasisFullRowModule",
            "IsSmallList", "IsMutableBasisByImmutableBasisRep", "CategoryCollections(IsAdditivelyCommutativeElement)", "HasIsCanonicalBasisFullMatrixModule", "IsLeftActedOnByDivisionRing", "IsCompletelyRegularSemigroup",
            "CategoryCollections(CategoryCollections(IsExtLElement))", "HasIsNilpotentByFinite", "CategoryCollections(IsNearAdditiveElementWithInverse)", "IsNilpotentByFinite", "HasBaseDomain", "HasIsNaturalSymmetricGroup",
            "HasOneOfBaseDomain", "IsTrivialLOpEOne", "IsNearAdditiveElement", "CategoryCollections(IsPerm)", "HasIsFinitelyGeneratedMagma", "IsDenseList", "CategoryCollections(IsExtAElement)", "IsVecOrMatObj",
            "IsComponentObjectRep", "IsGroupAsSemigroup", "IsMagmaWithOne", "IsCompletelySimpleSemigroup", "IsCyc", "HasIsTrivial", "IsMultiplicativeElementWithOne", "CategoryCollections(IsMultiplicativeElementWithInverse)",
            "IsRegularSemigroup", "IsPositionalObjectRep", "IsZmodnZMatrixRep", "HasIsAssociative", "IsInverseSemigroup", "CategoryCollections(IsFiniteOrderElement)", "HasDirectProductInfo", "IsExternalSubset",
            "IsAdditivelyCommutativeElement", "IsFieldControlledByGaloisGroup", "HasIsCompletelyRegularSemigroup", "IsIntegralRing", "HasAsList", "IsFinitelyGeneratedGroup", "IsElementOfFpGroup", "HasStandardGeneratorsSubringSCRing",
            "IsExtAElement", "HasBasis", "IsInternalRep", "KnowsHowToDecompose", "HasInt", "HasCanEasilyCompareElements", "HasIsLeftActedOnByDivisionRing", "IsRowListMatrix", "HasNumberColumns", "IsAssociativeLOpEProd",
            "HasNumberRows", "IsAssociativeElement", "HasSin", "IsMagmaWithInverses", "HasIsSimpleSemigroup", "CategoryCollections(CategoryCollections(CategoryCollections(IsNearAdditiveElementWithInverse)))", "IsFiniteDimensional",
            "HasIsFiniteDimensional", "IsNoImmediateMethodsObject", "HasOrder", "HasIsRDistributive", "IsDuplicateFree", "HasIsSymmetricGroup", "IsSymmetricGroup", "HasIsSubsetLocallyFiniteGroup", "HasIsNonTrivial", "IsFFE",
            "HasCoth", "CategoryCollections(IsFFE)", "CategoryCollections(((IsWord and IsAssociativeElement) and (IsWord and IsMultiplicativeElementWithInverse)))", "HasIsGroupAsSemigroup", "IsNearAdditiveGroup", "HasIsIntegralRing",
            "IsFreeLeftModule", "HasIsCanonicalBasisFullRowModule", "IsMagmaWithInversesIfNonzero", "CategoryCollections(CategoryCollections(IsExtAElement))", "IsNearAdditiveElementWithZero", "IsCommutativeElement",
            "CategoryCollections(IsMultiplicativeElementWithOne)", "HasIsInfiniteAbelianizationGroup", "IsDistributiveLOpDSum", "HasCosh", "IsMutableBasis", "HasIsFinite", "HasIsOrthodoxSemigroup", "IsLeftActedOnByRing", "IsZmodnZObj",
            "IsAttributeStoringRep", "IsMultiplicativeElement", "CategoryCollections(CategoryCollections(IsExtRElement))", "IsGeneratorsOfMagmaWithInverses", "IsNearAdditiveMagma", "IsAssociative", "IsIterator"
    };

}
