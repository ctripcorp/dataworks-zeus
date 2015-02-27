mini = {
    components: {},
    uids: {},
    ux: {},
    isReady: false,
    byClass: function (_, $) {
        if (typeof $ == "string") $ = E1R($);
        return jQuery("." + _, $)[0]
    },
    getComponents: function () {
        var componentArr = [];
        for (var component in mini.components) {
            var $ = mini.components[component];
            componentArr.push($)
        }
        return componentArr
    },
    get: function (_) {
        if (!_) return null;
        if (mini.isControl(_)) return _;
        if (typeof _ == "string") if (_.charAt(0) == "#") _ = _.substr(1);
        if (typeof _ == "string") return mini.components[_];
        else {
            var $ = mini.uids[_.uid];
            if ($ && $.el == _) return $
        }
        return null
    },
    getbyUID: function ($) {
        return mini.uids[$]
    },
    findControls: function (E, B) {
        if (!E) return [];
        B = B || mini;
        var $ = [],
		D = mini.uids;
        for (var A in D) {
            var _ = D[A],
			C = E["call"](B, _);
            if (C === true || C === 1) {
                $.push(_);
                if (C === 1) break
            }
        }
        return $
    },
    emptyFn: function () { },
    createNameControls: function (A, F) {
        if (!A || !A.el) return;
        if (!F) F = "_";
        var C = A.el,
		$ = mini.findControls(function ($) {
		    if (!$.el || !$.name) return false;
		    if (Yma(C, $.el)) return true;
		    return false
		});
        for (var _ = 0, D = $.length; _ < D; _++) {
            var B = $[_],
			E = F + B.name;
            if (F === true) E = B.name[0].toUpperCase() + B.name.substring(1, B.name.length);
            A[E] = B
        }
    },
    getbyName: function (C, _) {
        var B = mini.isControl(_),
		A = _;
        if (_ && B) _ = _.el;
        _ = E1R(_);
        _ = _ || document.body;
        var $ = this.findControls(function ($) {
            if (!$.el) return false;
            if ($.name == C && Yma(_, $.el)) return 1;
            return false
        },
		this);
        if (B && $.length == 0 && A && A.getbyName) return A.getbyName(C);
        return $[0]
    },
    getParams: function (C) {
        if (!C) C = location.href;
        C = C.split("?")[1];
        var B = {};
        if (C) {
            var A = C.split("&");
            for (var _ = 0, D = A.length; _ < D; _++) {
                var $ = A[_].split("=");
                B[$[0]] = decodeURIComponent($[1])
            }
        }
        return B
    },
    reg: function ($) {
        this.components[$.id] = $;
        this.uids[$.uid] = $
    },
    unreg: function ($) {
        delete mini.components[$.id];
        delete mini.uids[$.uid]
    },
    classes: {},
    uiClasses: {},
    getClass: function ($) {
        if (!$) return null;
        return this.classes[$.toLowerCase()]
    },
    getClassByUICls: function ($) {
        return this.uiClasses[$.toLowerCase()]
    },
    idPre: "mini-",
    idIndex: 1,
    newId: function ($) {
        return ($ || this.idPre) + this.idIndex++
    },
    //将a中的属性添加到$中
    copyTo: function ($, A) {
        if ($ && A) for (var _ in A) $[_] = A[_];
        return $
    },
    copyIf: function ($, A) {
        if ($ && A) for (var _ in A) if (mini.isNull($[_])) $[_] = A[_];
        return $
    },
    createDelegate: function (_, $) {
        if (!_) return function () { };
        return function () {
            return _.apply($, arguments)
        }
    },
    isControl: function ($) {
        return !!($ && $.isControl)
    },
    isElement: function ($) {
        return $ && $.appendChild
    },
    isDate: function ($) {
        return $ && $.getFullYear
    },
    isArray: function ($) {
        return $ && !!$.unshift
    },
    isNull: function ($) {
        return $ === null || $ === undefined
    },
    isNumber: function ($) {
        return !isNaN($) && typeof $ == "number"
    },
    isEquals: function ($, _) {
        if ($ !== 0 && _ !== 0) if ((mini.isNull($) || $ == "") && (mini.isNull(_) || _ == "")) return true;
        if ($ && _ && $.getFullYear && _.getFullYear) return $.getTime() === _.getTime();
        if (typeof $ == "object" && typeof _ == "object" && $ === _) return true;
        return String($) === String(_)
    },
    forEach: function (E, D, B) {
        var _ = E.clone();
        for (var i = 0; i< _.length; i++) {
            var $ = _[i];
            if (D["call"](B, $, i, E) === false) break
        }
    },
    sort: function (A, _, $) {
        $ = $ || A;
        A.sort(_)
    },
    removeNode: function ($) {
        jQuery($).remove()
    },
    elWarp: document.createElement("div")
};
HbyG = function (A, _) {
    _ = _.toLowerCase();
    if (!mini.classes[_]) {
        mini.classes[_] = A;
        A["prototype"].type = _
    }
    var $ = A["prototype"].uiCls;
    if (!mini.isNull($) && !mini.uiClasses[$]) mini.uiClasses[$] = A
};
Pv_r = function (E, A, $) {
    if (typeof A != "function") return this;
    var D = E,
	C = D.prototype,
	_ = A["prototype"];
    if (D["superclass"] == _) return;
    D["superclass"] = _;
    D["superclass"]["constructor"] = A;
    for (var B in _) C[B] = _[B];
    if ($) for (B in $) C[B] = $[B];
    return D
};
mini.copyTo(mini, {
    extend: Pv_r,
    regClass: HbyG,
    debug: false
});
Om = [];
KJ_T = function (_, $) {
    Om.push([_, $]);
    if (!mini._EventTimer) mini._EventTimer = setTimeout(function () {
        JXG()
    },
	1)
};
JXG = function () {
    for (var $ = 0, _ = Om.length; $ < _; $++) {
        var A = Om[$];
        A[0]["call"](A[1])
    }
    Om = [];
    mini._EventTimer = null
};
JjOd = function (C) {
    if (typeof C != "string") return null;
    var _ = C.split("."),
	D = null;
    for (var $ = 0, A = _.length; $ < A; $++) {
        var B = _[$];
        if (!D) D = window[B];
        else D = D[B];
        if (!D) break
    }
    return D
};
mini.getAndCreate = function ($) {
    if (!$) return null;
    if (typeof $ == "string") return mini.components[$];
    if (typeof $ == "object") if (mini.isControl($)) return $;
    else if (mini.isElement($)) return mini.uids[$.uid];
    else return mini.create($);
    return null
};
mini.create = function ($) {
    if (!$) return null;
    if (mini.get($.id) === $) return $;
    var _ = this.getClass($.type);
    if (!_) return null;
    var A = new _();
    A.set($);
    return A
};
mini.append = function (_, A) {
    _ = E1R(_);
    if (!A || !_) return;
    if (typeof A == "string") {
        if (A.charAt(0) == "#") {
            A = E1R(A);
            if (!A) return;
            _.appendChild(A);
            return A
        } else {
            if (A.indexOf("<tr") == 0) {
                return jQuery(_).append(A)[0].lastChild;
                return
            }
            var $ = document.createElement("div");
            $.innerHTML = A;
            A = $.firstChild;
            while ($.firstChild) _.appendChild($.firstChild);
            return A
        }
    } else {
        _.appendChild(A);
        return A
    }
};
mini.prepend = function (_, A) {
    if (typeof A == "string") if (A.charAt(0) == "#") A = E1R(A);
    else {
        var $ = document.createElement("div");
        $.innerHTML = A;
        A = $.firstChild
    }
    return jQuery(_).prepend(A)[0].firstChild
};
//var 
//Aph = "getBottomVisibleColumns",
//YX = "setFrozenStartColumn",
//Jj = "showCollapseButton",
//NgR = "showFolderCheckBox",
//PF75 = "setFrozenEndColumn",
//AwTL = "getAncestorColumns",
//RT = "getFilterRowHeight",
//TKb = "checkSelectOnLoad",
//LDiY = "frozenStartColumn",
//FK6Y = "allowResizeColumn",
//HVO = "showExpandButtons",
//Vti = "requiredErrorText",
//Dspk = "getMaxColumnLevel",
//Yly = "isAncestorColumn",
//V2gz = "allowAlternating",
//IvNS = "getBottomColumns",
//HlsM = "isShowRowDetail",
//FS2f = "allowCellSelect",
//X1 = "showAllCheckBox",
//T$Y = "frozenEndColumn",
//Rjo = "allowMoveColumn",
//TI = "allowSortColumn",
//$MK = "refreshOnExpand",
//CfQ = "showCloseButton",
//YV00 = "unFrozenColumns",
//GE3 = "getParentColumn",
//EQmT = "isVisibleColumn",
//IH$ = "getFooterHeight",
//LB = "getHeaderHeight",
//KvqX = "_createColumnId",
//Hjc = "getRowDetailEl",
//$uu = "scrollIntoView",
//JbG$ = "setColumnWidth",
//KEz = "setCurrentCell",
//YavM = "allowRowSelect",
//_Mh = "showSummaryRow",
//Vi$_ = "showVGridLines",
//_Xnk = "showHGridLines",
//LCy = "checkRecursive",
//A0d = "enableHotTrack",
//MO7 = "popupMaxHeight",
//HvcZ = "popupMinHeight",
//R6q = "refreshOnClick",
//O46I = "getColumnWidth",
//SQG = "getEditRowData",
//QRD = "getParentNode",
//K3K = "removeNodeCls",
//MbH = "showRowDetail",
//Ji7 = "hideRowDetail",
//P_mN = "commitEditRow",
//QrjI = "beginEditCell",
//O3wr = "allowCellEdit",
//CX = "decimalPlaces",
//YfL = "showFilterRow",
//HW = "dropGroupName",
//FAb = "dragGroupName",
//Telh = "showTreeLines",
//RR$ = "popupMaxWidth",
//KXuV = "popupMinWidth",
//VA = "showMinButton",
//YssP = "showMaxButton",
//Nmj = "getChildNodes",
//L8l = "getCellEditor",
//HI9 = "cancelEditRow",
//E_A = "getRowByValue",
//NWF = "removeItemCls",
//Z_3 = "_createCellId",
//AJ9F = "_createItemId",
//G7k = "setValueField",
//GiQ = "getAncestors",
//V8p4 = "collapseNode",
//PzoQ = "removeRowCls",
//IXr = "getColumnBox",
//_Rp = "showCheckBox",
//HzZ = "autoCollapse",
//M4 = "showTreeIcon",
//ACw = "checkOnClick",
//Ud_ = "defaultValue",
//Yuv = "resultAsData",
//APUU = "resultAsTree",
//DZA = "_ParseString",
//Qcu = "getItemValue",
//XV1r = "_createRowId",
//O8L = "isAutoHeight",
//XYW = "findListener",
//Oe0 = "getRegionEl",
//ZJ = "removeClass",
//FH = "isFirstNode",
//NTW = "getSelected",
//Lq = "setSelected",
//Z$ = "multiSelect",
//QR3e = "tabPosition",
//LjTp = "columnWidth",
//Zsu = "handlerSize",
//J8w = "allowSelect",
//Hl_ = "popupHeight",
//__H9 = "contextMenu",
//CYg = "borderStyle",
//_BvC = "parentField",
//Rj7O = "closeAction",
//Gqr = "_rowIdField",
//Vja = "allowResize",
//AN = "showToolbar",
//RTMl = "deselectAll",
//FNf = "treeToArray",
//Io$O = "eachColumns",
//J0 = "getItemText",
//FL = "isAutoWidth",
//CHk = "_initEvents",
//_kqU = "constructor",
//HLkW = "addNodeCls",
//YzFk = "expandNode",
//MeF = "setColumns",
//Pf = "cancelEdit",
//JH9 = "moveColumn",
//$KM2 = "removeNode",
//Nsbf = "setCurrent",
//_Xi = "totalCount",
//Sny = "popupWidth",
//URR = "titleField",
//Ke = "valueField",
//$ONY = "showShadow",
//NqZG = "showFooter",
//K6YB = "findParent",
//XcD = "_getColumn",
//_8l = "_ParseBool",
//ByY = "clearEvent",
//Qud = "getCellBox",
//R1W = "selectText",
//WMc = "setVisible",
//YYr = "isGrouping",
//Tui = "addItemCls",
//MV1A = "isSelected",
//TYs = "isReadOnly",
//Mh = "superclass",
//Ry5 = "getRegion",
//Ubh = "isEditing",
//__ = "hidePopup",
//GKu = "removeRow",
//QBC = "addRowCls",
//P1 = "increment",
//GWl1 = "allowDrop",
//CG = "pageIndex",
//BA = "iconStyle",
//PIPm = "errorMode",
//U5g = "textField",
//MHiD = "groupName",
//KVg = "showEmpty",
//WV2 = "emptyText",
//ZY2O = "showModal",
//_bMF = "getColumn",
//JqH = "getHeight",
//Atd = "_ParseInt",
//CrG = "showPopup",
//J5y = "updateRow",
//Bsc = "deselects",
//Y8 = "isDisplay",
//DGkO = "setHeight",
//Pm0 = "removeCls",
//Oj = "prototype",
//QlE = "addClass",
//ZD8 = "isEquals",
//A8I = "maxValue",
//Tg4 = "minValue",
//NM6 = "showBody",
//RnT = "tabAlign",
//QUAS = "sizeList",
//DX_K = "pageSize",
//PI = "urlField",
//Xw = "readOnly",
//TU = "getWidth",
//KRj = "isFrozen",
//L1G = "loadData",
//Xfm = "deselect",
//OP = "setValue",
//RfI = "validate",
//K0 = "getAttrs",
//HPZf = "setWidth",
//W4 = "doUpdate",
//Nm6 = "doLayout",
//QZo = "renderTo",
//Sp$ = "setText",
//ABWV = "idField",
//Udv = "getNode",
//CfL = "getItem",
//EN = "repaint",
//FrOp = "selects",
//Cy = "setData",
//TN = "_create",
//VKN = "destroy",
//YFWl = "jsName",
//Nf7 = "getRow",
//Pssj = "select",
//Dot8 = "within",
//XzXg = "addCls",
//Aupz = "render",
//TA0 = "setXY";
//Wi = "call";
mini.Component = function () {
    this.J1j = {};
    this.uid = mini.newId(this.idPre);
    if (!this.id) this.id = this.uid;
    mini.reg(this)
};
mini.Component["prototype"] = {
    isControl: true,
    id: null,
    idPre: "mini-",
    hasId: false,
    XF9X: true,
    set: function (A) {
        if (typeof A == "string") return this;
        var C = this.SX1V;
        this.SX1V = false;
        var B = A["renderTo"] || A["render"];
        delete A["renderTo"];
        delete A["render"];
        for (var $ in A) if ($.toLowerCase().indexOf("on") == 0) {
            var F = A[$];
            this.on($.substring(2, $.length).toLowerCase(), F);
            delete A[$]
        }
        for ($ in A) {
            var E = A[$],
			D = "set" + $.charAt(0).toUpperCase() + $.substring(1, $.length),
			_ = this[D];
            if (_) _["call"](this, E);
            else this[$] = E
        }
        if (B && this["render"]) this["render"](B);
        this.SX1V = C;
        if (this["doLayout"]) this["doLayout"]();
        return this
    },
    fire: function (A, B) {
        if (this.XF9X == false) return;
        A = A.toLowerCase();
        var _ = this.J1j[A];
        if (_) {
            if (!B) B = {};
            if (B && B != this) {
                B.source = B.sender = this;
                if (!B.type) B.type = A
            }
            for (var $ = 0, D = _.length; $ < D; $++) {
                var C = _[$];
                if (C) C[0].apply(C[1], [B])
            }
        }
    },
    on: function (type, fn, scope) {
        if (typeof fn == "string") {
            var f = JjOd(fn);
            if (!f) {
                var id = mini.newId("__str_");
                window[id] = fn;
                eval("fn = function(e){var s = " + id + ";var fn = JjOd(s); if(fn) {fn['call'](this,e)}else{eval(s);}}")
            } else fn = f
        }
        if (typeof fn != "function" || !type) return false;
        type = type.toLowerCase();
        var event = this.J1j[type];
        if (!event) event = this.J1j[type] = [];
        scope = scope || this;
        if (!this["findListener"](type, fn, scope)) event.push([fn, scope]);
        return this
    },
    un: function ($, C, _) {
        if (typeof C != "function") return false;
        $ = $.toLowerCase();
        var A = this.J1j[$];
        if (A) {
            _ = _ || this;
            var B = this["findListener"]($, C, _);
            if (B) A.remove(B)
        }
        return this
    },
    findListener: function (A, E, B) {
        A = A.toLowerCase();
        B = B || this;
        var _ = this.J1j[A];
        if (_) for (var $ = 0, D = _.length; $ < D; $++) {
            var C = _[$];
            if (C[0] === E && C[1] === B) return C
        }
    },
    setId: function ($) {
        if (!$) throw new Error("id not null");
        if (this.hasId) throw new Error("id just set only one");
        mini["unreg"](this);
        this.id = $;
        if (this.el) this.el.id = $;
        if (this.VXt) this.VXt.id = $ + "$text";
        if (this.Izde) this.Izde.id = $ + "$value";
        this.hasId = true;
        mini.reg(this)
    },
    getId: function () {
        return this.id
    },
    destroy: function () {
        mini["unreg"](this);
        this.fire("destroy")
    }
};
mini.Control = function () {
    mini.Control["superclass"]["constructor"]["call"](this);
    this["_create"]();
    this.el.uid = this.uid;
    this["_initEvents"]();
    if (this._clearBorder) this.el.style.borderWidth = "0";
    this["addCls"](this.uiCls);
    this["setWidth"](this.width);
    this["setHeight"](this.height);
    this.el.style.display = this.visible ? this.U60X : "none"
};
//Pv_r = function (子组件E, 父组件及构造方法A, 新增属性$) {
//    if (typeof 父组件及构造方法A != "function") return this;
//    var 子组件D = 子组件E,
//	子组件原型C = 子组件D.prototype,
//	父组件原型_ = 父组件及构造方法A["prototype"];
//    if (子组件D["superclass"] == 父组件原型_) return;
//    子组件D["superclass"] = 父组件原型_;
//    子组件D["superclass"]["constructor"] = 父组件及构造方法A;
//    for (var B in 父组件原型_) 子组件原型C[B] = 父组件原型_[B];
//    if (新增属性$) for (B in 新增属性$) 子组件原型C[B] = 新增属性$[B];
//    return 子组件D
//};
Pv_r(mini.Control, mini.Component, {
    jsName: null,
    width: "",
    height: "",
    visible: true,
    readOnly: false,
    enabled: true,
    tooltip: "",
    AOf: "mini-readonly",
    ZSXk: "mini-disabled",
    _create: function () {
        this.el = document.createElement("div")
    },
    _initEvents: function () { },
    within: function ($) {
        if (Yma(this.el, $.target)) return true;
        return false
    },
    name: "",
    setName: function ($) {
        this.name = $
    },
    getName: function () {
        return this.name
    },
    isAutoHeight: function () {
        var $ = this.el.style.height;
        return $ == "auto" || $ == ""
    },
    isAutoWidth: function () {
        var $ = this.el.style.width;
        return $ == "auto" || $ == ""
    },
    isFixedSize: function () {
        var $ = this.width,
		_ = this.height;
        if (parseInt($) + "px" == $ && parseInt(_) + "px" == _) return true;
        return false
    },
    isRender: function ($) {
        return !!(this.el && this.el.parentNode && this.el.parentNode.tagName)
    },
    render: function (_, $) {
        if (typeof _ === "string") if (_ == "#body") _ = document.body;
        else _ = E1R(_);
        if (!_) return;
        if (!$) $ = "append";
        $ = $.toLowerCase();
        if ($ == "before") jQuery(_).before(this.el);
        else if ($ == "preend") jQuery(_).preend(this.el);
        else if ($ == "after") jQuery(_).after(this.el);
        else _.appendChild(this.el);
        this.el.id = this.id;
        this["doLayout"]();
        this.fire("render")
    },
    getEl: function () {
        return this.el
    },
    setJsName: function ($) {
        this["jsName"] = $;
        window[$] = this
    },
    getJsName: function () {
        return this["jsName"]
    },
    setTooltip: function ($) {
        this.tooltip = $;
        this.el.title = $
    },
    getTooltip: function () {
        return this.tooltip
    },
    _sizeChaned: function () {
        this["doLayout"]()
    },
    setWidth: function ($) {
        if (parseInt($) == $) $ += "px";
        this.width = $;
        this.el.style.width = $;
        this._sizeChaned()
    },
    getWidth: function (_) {
        var $ = _ ? jQuery(this.el).width() : jQuery(this.el).outerWidth();
        if (_ && this._firstChild) {
            var A = Ly5(this._firstChild);
            $ = $ - A.left - A.right
        }
        return $
    },
    setHeight: function ($) {
        if (parseInt($) == $) $ += "px";
        this.height = $;
        this.el.style.height = $;
        this._sizeChaned()
    },
    getHeight: function (_) {
        var $ = _ ? jQuery(this.el).height() : jQuery(this.el).outerHeight();
        if (_ && this._firstChild) {
            var A = Ly5(this._firstChild);
            $ = $ - A.top - A.bottom
        }
        return $
    },
    getBox: function () {
        return EcG(this.el)
    },
    setBorderStyle: function ($) {
        var _ = this._firstChild || this.el;
        BIT(_, $);
        this["doLayout"]()
    },
    getBorderStyle: function () {
        return this["borderStyle"]
    },
    _clearBorder: true,
    setStyle: function ($) {
        this.style = $;
        BIT(this.el, $);
        if (this._clearBorder) this.el.style.borderWidth = "0";
        this.width = this.el.style.width;
        this.height = this.el.style.height;
        this._sizeChaned()
    },
    getStyle: function () {
        return this.style
    },
    setCls: function ($) {
        WeL(this.el, this.cls);
        RE(this.el, $);
        this.cls = $
    },
    getCls: function () {
        return this.cls
    },
    addCls: function ($) {
        RE(this.el, $)
    },
    removeCls: function ($) {
        WeL(this.el, $)
    },
    _doReadOnly: function () {
        if (this["readOnly"]) this["addCls"](this.AOf);
        else this["removeCls"](this.AOf)
    },
    setReadOnly: function ($) {
        this["readOnly"] = $;
        this._doReadOnly()
    },
    getReadOnly: function () {
        return this["readOnly"]
    },
    getParent: function (A) {
        var $ = document,
		B = this.el.parentNode;
        while (B != $ && B != null) {
            var _ = mini.get(B);
            if (_) {
                if (!mini.isControl(_)) return null;
                if (!A || _.uiCls == A) return _
            }
            B = B.parentNode
        }
        return null
    },
    isReadOnly: function () {
        if (this["readOnly"] || !this.enabled) return true;
        var $ = this.getParent();
        if ($) return $["isReadOnly"]();
        return false
    },
    setEnabled: function ($) {
        this.enabled = $;
        if (this.enabled) this["removeCls"](this.ZSXk);
        else this["addCls"](this.ZSXk);
        this._doReadOnly()
    },
    getEnabled: function () {
        return this.enabled
    },
    enable: function () {
        this.setEnabled(true)
    },
    disable: function () {
        this.setEnabled(false)
    },
    U60X: "",
    setVisible: function ($) {
        this.visible = $;
        if (this.el) {
            this.el.style.display = $ ? this.U60X : "none";
            this["doLayout"]()
        }
    },
    getVisible: function () {
        return this.visible
    },
    show: function () {
        this["setVisible"](true)
    },
    hide: function () {
        this["setVisible"](false)
    },
    isDisplay: function () {
        if (NTp == false) return false;
        var $ = document.body,
		_ = this.el;
        while (1) {
            if (_ == null || !_.style) return false;
            if (_ && _.style && _.style.display == "none") return false;
            if (_ == $) return true;
            _ = _.parentNode
        }
        return true
    },
    N2b: true,
    beginUpdate: function () {
        this.N2b = false
    },
    endUpdate: function () {
        this.N2b = true;
        this["doUpdate"]()
    },
    doUpdate: function () { },
    canLayout: function () {
        if (this.SX1V == false) return false;
        return this["isDisplay"]()
    },
    doLayout: function () { },
    layoutChanged: function () {
        if (this.canLayout() == false) return;
        this["doLayout"]()
    },
    destroy: function (_) {
        if (this.el);
        if (this.el) {
            mini["clearEvent"](this.el);
            if (_ !== false) {
                var $ = this.el.parentNode;
                if ($) $.removeChild(this.el)
            }
        }
        this._firstChild = null;
        this.el = null;
        mini["unreg"](this);
        this.fire("destroy")
    },
    focus: function () {
        try {
            var $ = this;
            $.el.focus()
        } catch (_) { }
    },
    blur: function () {
        try {
            var $ = this;
            $.el.blur()
        } catch (_) { }
    },
    allowAnim: true,
    setAllowAnim: function ($) {
        this.allowAnim = $
    },
    getAllowAnim: function () {
        return this.allowAnim
    },
    Gjy: function () {
        return this.el
    },
    mask: function ($) {
        if (typeof $ == "string") $ = {
            html: $
        };
        $ = $ || {};
        $.el = this.Gjy();
        if (!$.cls) $.cls = this.Buy;
        mini.mask($)
    },
    unmask: function () {
        mini.unmask(this.Gjy())
    },
    Buy: "mini-mask-loading",
    loadingMsg: "Loading...",
    loading: function ($) {
        this.mask($ || this.loadingMsg)
    },
    setLoadingMsg: function ($) {
        this.loadingMsg = $
    },
    getLoadingMsg: function () {
        return this.loadingMsg
    },
    _getContextMenu: function ($) {
        var _ = $;
        if (typeof $ == "string") {
            _ = mini.get($);
            if (!_) {
                mini.parse($);
                _ = mini.get($)
            }
        } else if (mini.isArray($)) _ = {
            type: "menu",
            items: $
        };
        else if (!mini.isControl($)) _ = mini.create($);
        return _
    },
    __OnHtmlContextMenu: function (_) {
        var $ = {
            popupEl: this.el,
            htmlEvent: _,
            cancel: false
        };
        this["contextMenu"].fire("BeforeOpen", $);
        if ($.cancel == true) return;
        this["contextMenu"].fire("opening", $);
        if ($.cancel == true) return;
        this["contextMenu"].showAtPos(_.pageX, _.pageY);
        this["contextMenu"].fire("Open", $);
        return false
    },
    contextMenu: null,
    setContextMenu: function ($) {
        var _ = this._getContextMenu($);
        if (!_) return;
        if (this["contextMenu"] !== _) {
            this["contextMenu"] = _;
            this["contextMenu"].owner = this;
            VNV(this.el, "contextmenu", this.__OnHtmlContextMenu, this)
        }
    },
    getContextMenu: function () {
        return this["contextMenu"]
    },
    setDefaultValue: function ($) {
        this["defaultValue"] = $
    },
    getDefaultValue: function () {
        return this["defaultValue"]
    },
    setValue: function ($) {
        this.value = $
    },
    getValue: function () {
        return this.value
    },
    TXVY: function ($) { },
    getAttrs: function (C) {
        var I = {},
		F = C.className;
        if (F) I.cls = F;
        mini["_ParseString"](C, I, ["id", "name", "width", "height", "borderStyle", "value", "defaultValue", "contextMenu", "tooltip"]);
        mini["_ParseBool"](C, I, ["visible", "enabled", "readOnly"]);
        if (C["readOnly"] && C["readOnly"] != "false") I["readOnly"] = true;
        var E = C.style.cssText;
        if (E) I.style = E;
        if (isIE9) {
            var _ = C.style.background;
            if (_) {
                if (!I.style) I.style = "";
                I.style += ";background:" + _
            }
        }
        if (this.style) if (I.style) I.style = this.style + ";" + I.style;
        else I.style = this.style;
        if (this["borderStyle"]) if (I["borderStyle"]) I["borderStyle"] = this["borderStyle"] + ";" + I["borderStyle"];
        else I["borderStyle"] = this["borderStyle"];
        var B = mini._attrs;
        if (B) for (var $ = 0, G = B.length; $ < G; $++) {
            var D = B[$],
			H = D[0],
			A = D[1];
            if (!A) A = "string";
            if (A == "string") mini["_ParseString"](C, I, [H]);
            else if (A == "bool") mini["_ParseBool"](C, I, [H]);
            else if (A == "int") mini["_ParseInt"](C, I, [H])
        }
        return I
    }
});
mini._attrs = null;
mini.regHtmlAttr = function (_, $) {
    if (!_) return;
    if (!$) $ = "string";
    if (!mini._attrs) mini._attrs = [];
    mini._attrs.push([_, $])
};
mini.ValidatorBase = function () {
    mini.ValidatorBase["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.ValidatorBase, mini.Control, {
    required: false,
    requiredErrorText: "This field is required.",
    F59: "mini-required",
    errorText: "",
    YNb: "mini-error",
    C4V: "mini-invalid",
    errorMode: "icon",
    validateOnChanged: true,
    Ss$: true,
    validate: function () {
        var $ = {
            value: this.getValue(),
            errorText: "",
            isValid: true
        };
        if (this.required) if (mini.isNull($.value) || $.value === "") {
            $.isValid = false;
            $.errorText = this["requiredErrorText"]
        }
        this.fire("validation", $);
        this.errorText = $.errorText;
        this.setIsValid($.isValid);
        return this.isValid()
    },
    isValid: function () {
        return this.Ss$
    },
    setIsValid: function ($) {
        this.Ss$ = $;
        this.BELF()
    },
    getIsValid: function () {
        return this.Ss$
    },
    setValidateOnChanged: function ($) {
        this.validateOnChanged = $
    },
    getValidateOnChanged: function ($) {
        return this.validateOnChanged
    },
    setErrorMode: function ($) {
        if (!$) $ = "none";
        this["errorMode"] = $.toLowerCase();
        if (this.Ss$ == false) this.BELF()
    },
    getErrorMode: function () {
        return this["errorMode"]
    },
    setErrorText: function ($) {
        this.errorText = $;
        if (this.Ss$ == false) this.BELF()
    },
    getErrorText: function () {
        return this.errorText
    },
    setRequired: function ($) {
        this.required = $;
        if (this.required) this["addCls"](this.F59);
        else this["removeCls"](this.F59)
    },
    getRequired: function () {
        return this.required
    },
    setRequiredErrorText: function ($) {
        this["requiredErrorText"] = $
    },
    getRequiredErrorText: function () {
        return this["requiredErrorText"]
    },
    errorIconEl: null,
    getErrorIconEl: function () {
        return this.PMX
    },
    Keo: function () { },
    BELF: function () {
        var $ = this;
        setTimeout(function () {
            $.O9f()
        },
		1)
    },
    O9f: function () {
        this["removeCls"](this.YNb);
        this["removeCls"](this.C4V);
        this.el.title = "";
        if (this.Ss$ == false) switch (this["errorMode"]) {
            case "icon":
                this["addCls"](this.YNb);
                var $ = this.getErrorIconEl();
                if ($) $.title = this.errorText;
                break;
            case "border":
                this["addCls"](this.C4V);
                this.el.title = this.errorText;
            default:
                this.Keo();
                break
        } else this.Keo();
        this["doLayout"]()
    },
    QWeD: function () {
        if (this.validateOnChanged) this["validate"]();
        this.fire("valuechanged", {
            value: this.getValue()
        })
    },
    onValueChanged: function (_, $) {
        this.on("valuechanged", _, $)
    },
    onValidation: function (_, $) {
        this.on("validation", _, $)
    },
    getAttrs: function (_) {
        var A = mini.ValidatorBase["superclass"]["getAttrs"]["call"](this, _);
        mini["_ParseString"](_, A, ["onvaluechanged", "onvalidation", "requiredErrorText", "errorMode"]);
        mini["_ParseBool"](_, A, ["validateOnChanged"]);
        var $ = _.getAttribute("required");
        if (!$) $ = _.required;
        if ($) A.required = $ != "false" ? true : false;
        return A
    }
});
mini.ListControl = function () {
    this.data = [];
    this.ABm9 = [];
    mini.ListControl["superclass"]["constructor"]["call"](this);
    this["doUpdate"]()
};
Pv_r(mini.ListControl, mini.ValidatorBase, {
    defaultValue: "",
    value: "",
    valueField: "id",
    textField: "text",
    delimiter: ",",
    data: null,
    url: "",
    YUz: "mini-list-item",
    Q13: "mini-list-item-hover",
    _Cm: "mini-list-item-selected",
    set: function (A) {
        if (typeof A == "string") return this;
        var $ = A.value;
        delete A.value;
        var B = A.url;
        delete A.url;
        var _ = A.data;
        delete A.data;
        mini.ListControl["superclass"].set["call"](this, A);
        if (!mini.isNull(_)) this["setData"](_);
        if (!mini.isNull(B)) this.setUrl(B);
        if (!mini.isNull($)) this["setValue"]($);
        return this
    },
    uiCls: "mini-list",
    _create: function () { },
    _initEvents: function () {
        KJ_T(function () {
            _pS(this.el, "click", this.YY, this);
            _pS(this.el, "dblclick", this.E8S, this);
            _pS(this.el, "mousedown", this.UNAE, this);
            _pS(this.el, "mouseup", this.ZXp, this);
            _pS(this.el, "mousemove", this.A$Z, this);
            _pS(this.el, "mouseover", this.Vv5, this);
            _pS(this.el, "mouseout", this.Gj$, this);
            _pS(this.el, "keydown", this.M$, this);
            _pS(this.el, "keyup", this.Y_VP, this);
            _pS(this.el, "contextmenu", this.JZg, this)
        },
		this)
    },
    destroy: function ($) {
        if (this.el) {
            this.el.onclick = null;
            this.el.ondblclick = null;
            this.el.onmousedown = null;
            this.el.onmouseup = null;
            this.el.onmousemove = null;
            this.el.onmouseover = null;
            this.el.onmouseout = null;
            this.el.onkeydown = null;
            this.el.onkeyup = null;
            this.el.oncontextmenu = null
        }
        mini.ListControl["superclass"]["destroy"]["call"](this, $)
    },
    name: "",
    setName: function ($) {
        this.name = $;
        if (this.Izde) mini.setAttr(this.Izde, "name", this.name)
    },
    H37a: function (_) {
        var A = MRj9(_.target, this.YUz);
        if (A) {
            var $ = parseInt(mini.getAttr(A, "index"));
            return this.data[$]
        }
    },
    addItemCls: function (_, A) {
        var $ = this.getItemEl(_);
        if ($) RE($, A)
    },
    removeItemCls: function (_, A) {
        var $ = this.getItemEl(_);
        if ($) WeL($, A)
    },
    getItemEl: function (_) {
        _ = this["getItem"](_);
        var $ = this.data.indexOf(_),
		A = this.PNIi($);
        return document.getElementById(A)
    },
    M9: function (_, $) {
        _ = this["getItem"](_);
        if (!_) return;
        var A = this.getItemEl(_);
        if ($ && A) this["scrollIntoView"](_);
        if (this.FNnWItem == _) return;
        this.KiX();
        this.FNnWItem = _;
        RE(A, this.Q13)
    },
    KiX: function () {
        if (!this.FNnWItem) return;
        var $ = this.getItemEl(this.FNnWItem);
        if ($) WeL($, this.Q13);
        this.FNnWItem = null
    },
    getFocusedItem: function () {
        return this.FNnWItem
    },
    getFocusedIndex: function () {
        return this.data.indexOf(this.FNnWItem)
    },
    Ku4: null,
    scrollIntoView: function (_) {
        try {
            var $ = this.getItemEl(_),
			A = this.Ku4 || this.el;
            mini["scrollIntoView"]($, A, false)
        } catch (B) { }
    },
    getItem: function ($) {
        if (typeof $ == "object") return $;
        if (typeof $ == "number") return this.data[$];
        return this.findItems($)[0]
    },
    getCount: function () {
        return this.data.length
    },
    indexOf: function ($) {
        return this.data.indexOf($)
    },
    getAt: function ($) {
        return this.data[$]
    },
    updateItem: function ($, _) {
        $ = this["getItem"]($);
        if (!$) return;
        mini.copyTo($, _);
        this["doUpdate"]()
    },
    load: function ($) {
        if (typeof $ == "string") this.setUrl($);
        else this["setData"]($)
    },
    loadData: function ($) {
        this["setData"]($)
    },
    setData: function (data) {
        if (typeof data == "string") data = eval(data);
        if (!mini.isArray(data)) data = [];
        this.data = data;
        this["doUpdate"]();
        if (this.value != "") {
            this["deselectAll"]();
            var records = this.findItems(this.value);
            this["selects"](records)
        }
    },
    getData: function () {
        return this.data.clone()
    },
    setUrl: function ($) {
        this.url = $;
        this.Sc({})
    },
    getUrl: function () {
        return this.url
    },
    Sc: function (params) {
        try {
            this.url = eval(this.url)
        } catch (e) { }
        var e = {
            url: this.url,
            async: false,
            type: "get",
            params: params,
            cancel: false
        };
        this.fire("beforeload", e);
        if (e.cancel == true) return;
        var sf = this;
        this.AJy = jQuery.ajax({
            url: e.url,
            async: e.async,
            data: e.params,
            type: e.type,
            cache: false,
            dataType: "text",
            success: function ($) {
                var _ = null;
                try {
                    _ = mini.decode($)
                } catch (A) { }
                var A = {
                    data: _,
                    cancel: false
                };
                sf.fire("preload", A);
                if (A.cancel == true) return;
                sf["setData"](A.data);
                sf.fire("load");
                setTimeout(function () {
                    sf["doLayout"]()
                },
				100)
            },
            error: function ($, A, _) {
                var B = {
                    xmlHttp: $,
                    errorCode: A
                };
                sf.fire("loaderror", B)
            }
        })
    },
    setValue: function ($) {
        if (mini.isNull($)) $ = "";
        if (this.value !== $) {
            var _ = this.findItems(this.value);
            this["deselects"](_);
            this.value = $;
            if (this.Izde) this.Izde.value = $;
            _ = this.findItems(this.value);
            this["selects"](_)
        }
    },
    getValue: function () {
        return this.value
    },
    getFormValue: function () {
        return this.value
    },
    setValueField: function ($) {
        this["valueField"] = $
    },
    getValueField: function () {
        return this["valueField"]
    },
    setTextField: function ($) {
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    getItemValue: function ($) {
        return String($[this.valueField])
    },
    getItemText: function ($) {
        var _ = $[this.textField];
        return mini.isNull(_) ? "" : String(_)
    },
    B0$Z: function (A) {
        if (mini.isNull(A)) A = [];
        if (!mini.isArray(A)) A = this.findItems(A);
        var B = [],
		C = [];
        for (var _ = 0, D = A.length; _ < D; _++) {
            var $ = A[_];
            if ($) {
                B.push(this["getItemValue"]($));
                C.push(this["getItemText"]($))
            }
        }
        return [B.join(this.delimiter), C.join(this.delimiter)]
    },
    findItems: function (B) {
        if (mini.isNull(B) || B === "") return [];
        var E = String(B).split(this.delimiter),
		D = this.data,
		H = {};
        for (var F = 0, A = D.length; F < A; F++) {
            var _ = D[F],
			I = _[this.valueField];
            H[I] = _
        }
        var C = [];
        for (var $ = 0, G = E.length; $ < G; $++) {
            I = E[$],
			_ = H[I];
            if (_) C.push(_)
        }
        return C
    },
    RrP: null,
    ABm9: [],
    multiSelect: false,
    JVD: function () {
        for (var _ = this.ABm9.length - 1; _ >= 0; _--) {
            var $ = this.ABm9[_];
            if (this.data.indexOf($) == -1) this.ABm9.removeAt(_)
        }
        var A = this.B0$Z(this.ABm9);
        this.value = A[0];
        if (this.Izde) this.Izde.value = this.value
    },
    setMultiSelect: function ($) {
        this["multiSelect"] = $
    },
    getMultiSelect: function () {
        return this["multiSelect"]
    },
    isSelected: function ($) {
        if (!$) return false;
        return this.ABm9.indexOf($) != -1
    },
    getSelecteds: function () {
        return this.ABm9.clone()
    },
    setSelected: function ($) {
        if ($) {
            this.RrP = $;
            this["select"]($)
        }
    },
    getSelected: function () {
        return this.RrP
    },
    select: function ($) {
        $ = this["getItem"]($);
        if (!$) return;
        if (this["isSelected"]($)) return;
        this["selects"]([$])
    },
    deselect: function ($) {
        $ = this["getItem"]($);
        if (!$) return;
        if (!this["isSelected"]($)) return;
        this["deselects"]([$])
    },
    selectAll: function () {
        var $ = this.data.clone();
        this["selects"]($)
    },
    deselectAll: function () {
        this["deselects"](this.ABm9)
    },
    clearSelect: function () {
        this["deselectAll"]()
    },
    selects: function (A) {
        if (!A || A.length == 0) return;
        A = A.clone();
        for (var _ = 0, C = A.length; _ < C; _++) {
            var $ = A[_];
            if (!this["isSelected"]($)) this.ABm9.push($)
        }
        var B = this;
        setTimeout(function () {
            B.YQU()
        },
		1)
    },
    deselects: function (A) {
        if (!A || A.length == 0) return;
        A = A.clone();
        for (var _ = A.length - 1; _ >= 0; _--) {
            var $ = A[_];
            if (this["isSelected"]($)) this.ABm9.remove($)
        }
        var B = this;
        setTimeout(function () {
            B.YQU()
        },
		1)
    },
    YQU: function () {
        var C = this.B0$Z(this.ABm9);
        this.value = C[0];
        if (this.Izde) this.Izde.value = this.value;
        for (var A = 0, D = this.data.length; A < D; A++) {
            var _ = this.data[A],
			F = this["isSelected"](_);
            if (F) this["addItemCls"](_, this._Cm);
            else this["removeItemCls"](_, this._Cm);
            var $ = this.data.indexOf(_),
			E = this.HDA($),
			B = document.getElementById(E);
            if (B) B.checked = !!F
        }
    },
    NAF: function (_, B) {
        var $ = this.B0$Z(this.ABm9);
        this.value = $[0];
        if (this.Izde) this.Izde.value = this.value;
        var A = {
            selecteds: this.getSelecteds(),
            selected: this["getSelected"](),
            value: this.getValue()
        };
        this.fire("SelectionChanged", A)
    },
    HDA: function ($) {
        return this.uid + "$ck$" + $
    },
    PNIi: function ($) {
        return this.uid + "$" + $
    },
    YY: function ($) {
        this.ZP($, "Click")
    },
    E8S: function ($) {
        this.ZP($, "Dblclick")
    },
    UNAE: function ($) {
        this.ZP($, "MouseDown")
    },
    ZXp: function ($) {
        this.ZP($, "MouseUp")
    },
    A$Z: function ($) {
        this.ZP($, "MouseMove")
    },
    Vv5: function ($) {
        this.ZP($, "MouseOver")
    },
    Gj$: function ($) {
        this.ZP($, "MouseOut")
    },
    M$: function ($) {
        this.ZP($, "KeyDown")
    },
    Y_VP: function ($) {
        this.ZP($, "KeyUp")
    },
    JZg: function ($) {
        this.ZP($, "ContextMenu")
    },
    ZP: function (C, A) {
        if (!this.enabled) return;
        var $ = this.H37a(C);
        if (!$) return;
        var B = this["_OnItem" + A];
        if (B) B["call"](this, $, C);
        else {
            var _ = {
                item: $,
                htmlEvent: C
            };
            this.fire("item" + A, _)
        }
    },
    _OnItemClick: function ($, A) {
        if (this["isReadOnly"]() || this.enabled == false || $.enabled === false) {
            A.preventDefault();
            return
        }
        var _ = this.getValue();
        if (this["multiSelect"]) {
            if (this["isSelected"]($)) {
                this["deselect"]($);
                if (this.RrP == $) this.RrP = null
            } else {
                this["select"]($);
                this.RrP = $
            }
            this.NAF()
        } else if (!this["isSelected"]($)) {
            this["deselectAll"]();
            this["select"]($);
            this.RrP = $;
            this.NAF()
        }
        if (_ != this.getValue()) this.QWeD();
        var A = {
            item: $,
            htmlEvent: A
        };
        this.fire("itemclick", A)
    },
    Q$KV: true,
    _OnItemMouseOut: function ($, _) {
        if (!this.enabled) return;
        if (this.Q$KV) this.KiX();
        var _ = {
            item: $,
            htmlEvent: _
        };
        this.fire("itemmouseout", _)
    },
    _OnItemMouseMove: function ($, _) {
        if (!this.enabled || $.enabled === false) return;
        this.M9($);
        var _ = {
            item: $,
            htmlEvent: _
        };
        this.fire("itemmousemove", _)
    },
    onItemClick: function (_, $) {
        this.on("itemclick", _, $)
    },
    onItemMouseDown: function (_, $) {
        this.on("itemmousedown", _, $)
    },
    onBeforeLoad: function (_, $) {
        this.on("beforeload", _, $)
    },
    onLoad: function (_, $) {
        this.on("load", _, $)
    },
    onLoadError: function (_, $) {
        this.on("loaderror", _, $)
    },
    onPreLoad: function (_, $) {
        this.on("preload", _, $)
    },
    getAttrs: function (C) {
        var G = mini.ListControl["superclass"]["getAttrs"]["call"](this, C);
        mini["_ParseString"](C, G, ["url", "data", "value", "textField", "valueField", "onitemclick", "onitemmousemove", "onselectionchanged", "onitemdblclick", "onbeforeload", "onload", "onloaderror", "ondataload"]);
        mini["_ParseBool"](C, G, ["multiSelect"]);
        var E = G["valueField"] || this["valueField"],
		B = G["textField"] || this["textField"];
        if (C.nodeName.toLowerCase() == "select") {
            var D = [];
            for (var A = 0, F = C.length; A < F; A++) {
                var _ = C.options[A],
				$ = {};
                $[B] = _.text;
                $[E] = _.value;
                D.push($)
            }
            if (D.length > 0) G.data = D
        }
        return G
    }
});
mini._Layouts = {};
mini.layout = function ($, _) {
    function A(C) {
        var D = mini.get(C);
        if (D) {
            if (D["doLayout"]) if (!mini._Layouts[D.uid]) {
                mini._Layouts[D.uid] = D;
                if (_ !== false || D.isFixedSize() == false) D["doLayout"](false);
                delete mini._Layouts[D.uid]
            }
        } else {
            var E = C.childNodes;
            if (E) for (var $ = 0, F = E.length; $ < F; $++) {
                var B = E[$];
                A(B)
            }
        }
    }
    if (!$) $ = document.body;
    A($)
};
mini.applyTo = function (_) {
    _ = E1R(_);
    if (!_) return this;
    if (mini.get(_)) throw new Error("not applyTo a mini control");
    var $ = this["getAttrs"](_);
    delete $._applyTo;
    if (mini.isNull($["defaultValue"]) && !mini.isNull($.value)) $["defaultValue"] = $.value;
    var A = _.parentNode;
    if (A && this.el != _) A.replaceChild(this.el, _);
    this.set($);
    this.TXVY(_);
    return this
};
mini._doParse = function (G) {
    var F = G.nodeName.toLowerCase();
    if (!F) return;
    var B = G.className;
    if (B) {
        var $ = mini.get(G);
        if (!$) {
            var H = B.split(" ");
            for (var E = 0, C = H.length; E < C; E++) {
                var A = H[E],
				I = mini.getClassByUICls(A);
                if (I) {
                    var D = new I();
                    mini.applyTo["call"](D, G);
                    G = D.el;
                    break
                }
            }
        }
    }
    if (F == "select" || D4ge(G, "mini-menu") || D4ge(G, "mini-datagrid") || D4ge(G, "mini-treegrid") || D4ge(G, "mini-tree") || D4ge(G, "mini-button") || D4ge(G, "mini-textbox") || D4ge(G, "mini-buttonedit")) return;
    var J = mini["getChildNodes"](G, true);
    for (E = 0, C = J.length; E < C; E++) {
        var _ = J[E];
        if (_.nodeType == 1) if (_.parentNode == G) mini._doParse(_)
    }
};
mini._Removes = [];
mini.parse = function ($) {
    if (typeof $ == "string") {
        var A = $;
        $ = E1R(A);
        if (!$) $ = document.body
    }
    if ($ && !mini.isElement($)) $ = $.el;
    if (!$) $ = document.body;
    var _ = NTp;
    if (isIE) NTp = false;
    mini._doParse($);
    NTp = _;
    mini.layout($)
};
mini["_ParseString"] = function (B, A, E) {
    for (var $ = 0, D = E.length; $ < D; $++) {
        var C = E[$],
		_ = mini.getAttr(B, C);
        if (_) A[C] = _
    }
};
mini["_ParseBool"] = function (B, A, E) {
    for (var $ = 0, D = E.length; $ < D; $++) {
        var C = E[$],
		_ = mini.getAttr(B, C);
        if (_) A[C] = _ == "true" ? true : false
    }
};
mini["_ParseInt"] = function (B, A, E) {
    for (var $ = 0, D = E.length; $ < D; $++) {
        var C = E[$],
		_ = parseInt(mini.getAttr(B, C));
        if (!isNaN(_)) A[C] = _
    }
};
mini._ParseColumns = function (N) {
    var G = [],
	O = mini["getChildNodes"](N);
    for (var M = 0, H = O.length; M < H; M++) {
        var C = O[M],
		T = jQuery(C),
		D = {},
		J = null,
		K = null,
		_ = mini["getChildNodes"](C);
        if (_) for (var $ = 0, P = _.length; $ < P; $++) {
            var B = _[$],
			A = jQuery(B).attr("property");
            if (!A) continue;
            A = A.toLowerCase();
            if (A == "columns") {
                D.columns = mini._ParseColumns(B);
                jQuery(B).remove()
            }
            if (A == "editor" || A == "filter") {
                var F = B.className,
				R = F.split(" ");
                for (var L = 0, S = R.length; L < S; L++) {
                    var E = R[L],
					Q = mini.getClassByUICls(E);
                    if (Q) {
                        var I = new Q();
                        if (A == "filter") {
                            K = I["getAttrs"](B);
                            K.type = I.type
                        } else {
                            J = I["getAttrs"](B);
                            J.type = I.type
                        }
                        break
                    }
                }
                jQuery(B).remove()
            }
        }
        D.header = C.innerHTML;
        mini["_ParseString"](C, D, ["name", "header", "field", "editor", "filter", "renderer", "width", "type", "renderer", "headerAlign", "align", "headerCls", "cellCls", "headerStyle", "cellStyle", "displayField", "dateFormat", "listFormat", "mapFormat", "trueValue", "falseValue", "dataType", "vtype"]);
        mini["_ParseBool"](C, D, ["visible", "readOnly", "allowSort", "allowReisze", "allowMove", "allowDrag", "autoShowPopup"]);
        if (J) D.editor = J;
        if (K) D.filter = K;
        if (D.dataType) D.dataType = D.dataType.toLowerCase();
        G.push(D)
    }
    return G
};
mini._Columns = {};
mini["_getColumn"] = function ($) {
    var _ = mini._Columns[$.toLowerCase()];
    if (!_) return {};
    return _()
};
mini.IndexColumn = function ($) {
    return mini.copyTo({
        width: 30,
        cellCls: "",
        align: "center",
        draggable: false,
        init: function ($) {
            $.on("addrow", this.__OnIndexChanged, this);
            $.on("removerow", this.__OnIndexChanged, this);
            $.on("moverow", this.__OnIndexChanged, this);
            if ($.isTree) {
                $.on("loadnode", this.__OnIndexChanged, this);
                this._gridUID = $.uid;
                this["_rowIdField"] = "_id"
            }
        },
        getNumberId: function ($) {
            return this._gridUID + "$number$" + $[this._rowIdField]
        },
        createNumber: function ($, _) {
            if (mini.isNull($["pageIndex"])) return _ + 1;
            else return ($["pageIndex"] * $["pageSize"]) + _ + 1
        },
        renderer: function (A) {
            var $ = A.sender;
            if (this.draggable) {
                if (!A.cellStyle) A.cellStyle = "";
                A.cellStyle += ";cursor:move;"
            }
            var _ = "<div id=\"" + this.getNumberId(A.record) + "\">";
            if (mini.isNull($["pageIndex"])) _ += A.rowIndex + 1;
            else _ += ($["pageIndex"] * $["pageSize"]) + A.rowIndex + 1;
            _ += "</div>";
            return _
        },
        __OnIndexChanged: function (F) {
            var $ = F.sender,
			C = $.toArray();
            for (var A = 0, D = C.length; A < D; A++) {
                var _ = C[A],
				E = this.getNumberId(_),
				B = document.getElementById(E);
                if (B) B.innerHTML = this.createNumber($, A)
            }
        }
    },
	$)
};
mini._Columns["indexcolumn"] = mini.IndexColumn;
mini.CheckColumn = function ($) {
    return mini.copyTo({
        width: 30,
        cellCls: "mini-checkcolumn",
        headerCls: "mini-checkcolumn",
        _multiRowSelect: true,
        header: function ($) {
            var A = this.uid + "checkall",
			_ = "<input type=\"checkbox\" id=\"" + A + "\" />";
            if (this["multiSelect"] == false) _ = "";
            return _
        },
        getCheckId: function ($) {
            return this._gridUID + "$checkcolumn$" + $[this._rowIdField]
        },
        init: function ($) {
            $.on("selectionchanged", this.XF, this);
            $.on("HeaderCellClick", this.S7, this)
        },
        renderer: function (C) {
            var B = this.getCheckId(C.record),
			_ = C.sender["isSelected"](C.record),
			A = "checkbox",
			$ = C.sender;
            if ($["multiSelect"] == false) A = "radio";
            return "<input type=\"" + A + "\" id=\"" + B + "\" " + (_ ? "checked" : "") + " hidefocus style=\"outline:none;\" onclick=\"return false\"/>"
        },
        S7: function (B) {
            var $ = B.sender,
			A = $.uid + "checkall",
			_ = document.getElementById(A);
            if (_) if ($["multiSelect"]) {
                if (_.checked) $.selectAll();
                else $["deselectAll"]()
            } else {
                $["deselectAll"]();
                if (_.checked) $["select"](0)
            }
        },
        XF: function (G) {
            var $ = G.sender,
			C = $.toArray();
            for (var A = 0, D = C.length; A < D; A++) {
                var _ = C[A],
				F = $["isSelected"](_),
				E = $.uid + "$checkcolumn$" + _[$._rowIdField],
				B = document.getElementById(E);
                if (B) B.checked = F
            }
        }
    },
	$)
};
mini._Columns["checkcolumn"] = mini.CheckColumn;
mini.ExpandColumn = function ($) {
    return mini.copyTo({
        width: 30,
        cellCls: "",
        align: "center",
        draggable: false,
        cellStyle: "padding:0",
        renderer: function ($) {
            return "<a class=\"mini-grid-ecIcon\" href=\"javascript:#\" onclick=\"return false\"></a>"
        },
        init: function ($) {
            $.on("cellclick", this._TZR, this)
        },
        _TZR: function (A) {
            var $ = A.sender;
            if (A.column == this && $["isShowRowDetail"]) if (MRj9(A.htmlEvent.target, "mini-grid-ecIcon")) {
                var _ = $["isShowRowDetail"](A.record);
                if ($.autoHideRowDetail) $.hideAllRowDetail();
                if (_) $["hideRowDetail"](A.record);
                else $["showRowDetail"](A.record)
            }
        }
    },
	$)
};
mini._Columns["expandcolumn"] = mini.ExpandColumn;
QbIwColumn = function ($) {
    return mini.copyTo({
        _type: "checkboxcolumn",
        header: "#",
        headerAlign: "center",
        cellCls: "mini-checkcolumn",
        trueValue: true,
        falseValue: false,
        readOnly: false,
        getCheckId: function ($) {
            return this._gridUID + "$checkbox$" + $[this._rowIdField]
        },
        renderer: function (B) {
            var A = this.getCheckId(B.record),
			_ = B.record[B.field] == this.trueValue ? true : false,
			$ = "checkbox";
            return "<input type=\"" + $ + "\" id=\"" + A + "\" " + (_ ? "checked" : "") + " hidefocus style=\"outline:none;\" onclick=\"return false;\"/>"
        },
        init: function ($) {
            this.grid = $;
            $.on("cellclick",
			function (C) {
			    if (C.column == this) {
			        if (this["readOnly"]) return;
			        var B = this.getCheckId(C.record),
					A = C.htmlEvent.target;
			        if (A.id == B) {
			            C.cancel = false;
			            C.value = C.record[C.field];
			            $.fire("cellbeginedit", C);
			            if (C.cancel !== true) {
			                var _ = C.record[C.field] == this.trueValue ? this.falseValue : this.trueValue;
			                if ($.Was) $.Was(C.record, C.column, _)
			            }
			        }
			    }
			},
			this);
            var _ = parseInt(this.trueValue),
			A = parseInt(this.falseValue);
            if (!isNaN(_)) this.trueValue = _;
            if (!isNaN(A)) this.falseValue = A
        }
    },
	$)
};
mini._Columns["checkboxcolumn"] = QbIwColumn;
I5WColumn = function ($) {
    return mini.copyTo({
        renderer: function (M) {
            var _ = M.value ? String(M.value) : "",
			C = _.split(","),
			D = "id",
			J = "text",
			A = {},
			G = M.column.editor;
            if (G && G.type == "combobox") {
                var B = this._combobox;
                if (!B) {
                    if (mini.isControl(G)) B = G;
                    else B = mini.create(G);
                    this._combobox = B
                }
                D = B.getValueField();
                J = B.getTextField();
                A = this._valueMaps;
                if (!A) {
                    A = {};
                    var K = B.getData();
                    for (var H = 0, E = K.length; H < E; H++) {
                        var $ = K[H];
                        A[$[D]] = $
                    }
                    this._valueMaps = A
                }
            }
            var L = [];
            for (H = 0, E = C.length; H < E; H++) {
                var F = C[H],
				$ = A[F];
                if ($) {
                    var I = $[J] || "";
                    L.push(I)
                }
            }
            return L.join(",")
        }
    },
	$)
};
mini._Columns["comboboxcolumn"] = I5WColumn;
BJ5 = function ($) {
    this.owner = $;
    VNV(this.owner.el, "mousedown", this.UNAE, this)
};
BJ5["prototype"] = {
    UNAE: function (_) {
        if (D4ge(_.target, "mini-grid-resizeGrid") && this.owner["allowResize"]) {
            var $ = this.G0$f();
            $.start(_)
        }
    },
    G0$f: function () {
        if (!this._resizeDragger) this._resizeDragger = new mini.Drag({
            capture: true,
            onStart: mini.createDelegate(this.J_sA, this),
            onMove: mini.createDelegate(this.KsZ, this),
            onStop: mini.createDelegate(this.ZYd, this)
        });
        return this._resizeDragger
    },
    J_sA: function ($) {
        this.proxy = mini.append(document.body, "<div class=\"mini-grid-resizeProxy\"></div>");
        this.proxy.style.cursor = "se-resize";
        this.elBox = EcG(this.owner.el);
        Ggi(this.proxy, this.elBox)
    },
    KsZ: function (B) {
        var $ = this.owner,
		D = B.now[0] - B.init[0],
		_ = B.now[1] - B.init[1],
		A = this.elBox.width + D,
		C = this.elBox.height + _;
        if (A < $.minWidth) A = $.minWidth;
        if (C < $.minHeight) C = $.minHeight;
        if (A > $.maxWidth) A = $.maxWidth;
        if (C > $.maxHeight) C = $.maxHeight;
        mini.setSize(this.proxy, A, C)
    },
    ZYd: function ($, A) {
        if (!this.proxy) return;
        var _ = EcG(this.proxy);
        jQuery(this.proxy).remove();
        this.proxy = null;
        this.elBox = null;
        if (A) {
            this.owner["setWidth"](_.width);
            this.owner["setHeight"](_.height)
        }
    }
};
mini.__IFrameCreateCount = 1;
mini.createIFrame = function (C, D) {
    var F = "__iframe_onload" + mini.__IFrameCreateCount++;
    window[F] = _;
    var E = "<iframe style=\"width:100%;height:100%;\" onload=\"" + F + "()\"  frameborder=\"0\"></iframe>",
	$ = document.createElement("div"),
	B = mini.append($, E),
	G = false;
    setTimeout(function () {
        B.src = C;
        G = true
    },
	5);
    var A = true;
    function _() {
        if (G == false) return;
        setTimeout(function () {
            if (D) D(B, A);
            A = false
        },
		1)
    }
    B._ondestroy = function () {
        window[F] = mini.emptyFn;
        B.src = "";
        B._ondestroy = null;
        B = null
    };
    return B
};
Q0j = function (C) {
    if (typeof C == "string") C = {
        url: C
    };
    C = mini.copyTo({
        width: 700,
        height: 400,
        allowResize: true,
        allowModal: true,
        closeAction: "destroy",
        title: "",
        titleIcon: "",
        iconCls: "",
        iconStyle: "",
        bodyStyle: "padding:0",
        url: "",
        showCloseButton: true,
        showFooter: false
    },
	C);
    C["closeAction"] = "destroy";
    var $ = C.onload;
    delete C.onload;
    var B = C.ondestroy;
    delete C.ondestroy;
    var _ = C.url;
    delete C.url;
    var A = new mini.Window();
    A.set(C);
    A.load(_, $, B);
    A.show();
    return A
};
mini.open = function (B) {
    if (!B) return;
    B.Owner = window;
    var $ = [];
    function _(A) {
        if (A.mini) $.push(A);
        if (A.parent && A.parent != A) _(A.parent)
    }
    _(window);
    var A = $[$.length - 1];
    return A.Q0j(B)
};
mini.openTop = mini.open;
mini.getData = function (C, A, E, D, _) {
    var $ = mini.getText(C, A, E, D, _),
	B = mini.decode($);
    return B
};
mini.getText = function (B, A, D, C, _) {
    var $ = null;
    jQuery.ajax({
        url: B,
        data: A,
        async: false,
        type: _ ? _ : "get",
        cache: false,
        dataType: "text",
        success: function (A, _) {
            $ = A
        },
        error: C
    });
    return $
};
if (!window.mini_RootPath) mini_RootPath = "/";
AlC = function (B) {
    var A = document.getElementsByTagName("script"),
	D = "";
    for (var $ = 0, E = A.length; $ < E; $++) {
        var C = A[$].src;
        if (C.indexOf(B) != -1) {
            var F = C.split(B);
            D = F[0];
            break
        }
    }
    var _ = location.href;
    _ = _.split("#")[0];
    _ = _.split("?")[0];
    F = _.split("/");
    F.length = F.length - 1;
    _ = F.join("/");
    if (D.indexOf("http:") == -1 && D.indexOf("file:") == -1) D = _ + "/" + D;
    return D
};
if (!window.mini_JSPath) mini_JSPath = AlC("miniui.js");
mini.update = function (A, _) {
    if (typeof A == "string") A = {
        url: A
    };
    if (_) A.el = _;
    A = mini.copyTo({
        el: null,
        url: "",
        async: false,
        type: "get",
        cache: false,
        dataType: "text",
        success: function (_) {
            var B = A.el;
            if (B) {
                $(B).html(_);
                mini.parse(B)
            }
        },
        error: function ($, A, _) { }
    },
	A);
    jQuery.ajax(A)
};
mini.createSingle = function ($) {
    if (typeof $ == "string") $ = mini.getClass($);
    if (typeof $ != "function") return;
    var _ = $.single;
    if (!_) _ = $.single = new $();
    return _
};
mini.createTopSingle = function ($) {
    if (typeof $ != "function") return;
    var _ = $["prototype"].type;
    if (top && top != window && top.mini && top.mini.getClass(_)) return top.mini.createSingle(_);
    else return mini.createSingle($)
};
mini.sortTypes = {
    "string": function ($) {
        return String($).toUpperCase()
    },
    "date": function ($) {
        if (!$) return 0;
        if (mini.isDate($)) return $.getTime();
        return mini.parseDate(String($))
    },
    "float": function (_) {
        var $ = parseFloat(String(_).replace(/,/g, ""));
        return isNaN($) ? 0 : $
    },
    "int": function (_) {
        var $ = parseInt(String(_).replace(/,/g, ""), 10);
        return isNaN($) ? 0 : $
    }
};
mini._ValidateVType = function (G, $, K, H) {
    var F = G.split(";");
    for (var E = 0, C = F.length; E < C; E++) {
        var G = F[E].trim(),
		J = G.split(":"),
		A = J[0],
		_ = J[1];
        if (_) _ = _.split(",");
        else _ = [];
        var D = mini.VTypes[A];
        if (D) {
            var I = D($, _);
            if (I !== true) {
                K.isValid = false;
                var B = J[0] + "ErrorText";
                K.errorText = H[B] || mini.VTypes[B] || "";
                K.errorText = String.format(K.errorText, _[0], _[1], _[2], _[3], _[4]);
                break
            }
        }
    }
};
mini.VTypes = {
    requiredErrorText: "This field is required.",
    emailErrorText: "Please enter a valid email address.",
    urlErrorText: "Please enter a valid URL.",
    floatErrorText: "Please enter a valid number.",
    intErrorText: "Please enter only digits",
    dateErrorText: "Please enter a valid date. Date format is {0}",
    maxLengthErrorText: "Please enter no more than {0} characters.",
    minLengthErrorText: "Please enter at least {0} characters.",
    maxErrorText: "Please enter a value less than or equal to {0}.",
    minErrorText: "Please enter a value greater than or equal to {0}.",
    rangeLengthErrorText: "Please enter a value between {0} and {1} characters long.",
    rangeCharErrorText: "Please enter a value between {0} and {1} characters long.",
    rangeErrorText: "Please enter a value between {0} and {1}.",
    required: function (_, $) {
        if (mini.isNull(_) || _ === "") return false;
        return true
    },
    email: function (_, $) {
        if (_.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) != -1) return true;
        else return false
    },
    url: function (A, $) {
        function _(_) {
            _ = _.toLowerCase();
            var $ = "^((https|http|ftp|rtsp|mms)?://)" + "?(([0-9a-z_!~*'().&=+$%-]+:)?[0-9a-z_!~*'().&=+$%-]+@)?" + "(([0-9]{1,3}.){3}[0-9]{1,3}" + "|" + "([0-9a-z_!~*'()-]+.)*" + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]." + "[a-z]{2,6})" + "(:[0-9]{1,4})?" + "((/?)|" + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$",
			A = new RegExp($);
            if (A.test(_)) return (true);
            else return (false)
        }
        return _(A)
    },
    "int": function (A, _) {
        function $(_) {
            var $ = String(_);
            return $.length > 0 && !(/[^0-9]/).test($)
        }
        return $(A)
    },
    "float": function (A, _) {
        function $(_) {
            var $ = String(_);
            return $.length > 0 && !(/[^0-9.]/).test($)
        }
        return $(A)
    },
    "date": function (B, _) {
        if (!B) return false;
        var $ = null,
		A = _[0];
        if (A) {
            $ = mini.parseDate(B, A);
            if ($ && $.getFullYear) if (mini.formatDate($, A) == B) return true
        } else {
            $ = mini.parseDate(B, "yyyy-MM-dd");
            if (!$) $ = mini.parseDate(B, "yyyy/MM/dd");
            if (!$) $ = mini.parseDate(B, "MM/dd/yyyy");
            if ($ && $.getFullYear) return true
        }
        return false
    },
    maxLength: function (A, $) {
        var _ = parseInt($);
        if (!A || isNaN(_)) return true;
        if (A.length <= _) return true;
        else return false
    },
    minLength: function (A, $) {
        var _ = parseInt($);
        if (isNaN(_)) return true;
        if (A.length >= _) return true;
        else return false
    },
    rangeLength: function (B, _) {
        if (!B) return false;
        var $ = parseFloat(_[0]),
		A = parseFloat(_[1]);
        if (isNaN($) || isNaN(A)) return true;
        if ($ <= B.length && B.length <= A) return true;
        return false
    },
    rangeChar: function (G, B) {
        if (!G) return false;
        var A = parseFloat(B[0]),
		E = parseFloat(B[1]);
        if (isNaN(A) || isNaN(E)) return true;
        function C(_) {
            var $ = new RegExp("^[\u4e00-\u9fa5]+$");
            if ($.test(_)) return true;
            return false
        }
        var $ = 0,
		F = String(G).split("");
        for (var _ = 0, D = F.length; _ < D; _++) if (C(F[_])) $ += 2;
        else $ += 1;
        if (A <= $ && $ <= E) return true;
        return false
    },
    range: function (B, _) {
        B = parseFloat(B);
        if (isNaN(B)) return false;
        var $ = parseFloat(_[0]),
		A = parseFloat(_[1]);
        if (isNaN($) || isNaN(A)) return true;
        if ($ <= B && B <= A) return true;
        return false
    }
};
mini.emptyFn = function () { };
mini.Drag = function ($) {
    mini.copyTo(this, $)
};
mini.Drag["prototype"] = {
    onStart: mini.emptyFn,
    onMove: mini.emptyFn,
    onStop: mini.emptyFn,
    capture: false,
    fps: 20,
    event: null,
    delay: 80,
    start: function (_) {
        _.preventDefault();
        if (_) this.event = _;
        this.now = this.init = [this.event.pageX, this.event.pageY];
        var $ = document;
        VNV($, "mousemove", this.move, this);
        VNV($, "mouseup", this.stop, this);
        VNV($, "contextmenu", this.contextmenu, this);
        if (this.context) VNV(this.context, "contextmenu", this.contextmenu, this);
        this.trigger = _.target;
        mini.selectable(this.trigger, false);
        mini.selectable($.body, false);
        if (this.capture) if (isIE) this.trigger.setCapture(true);
        else if (document.captureEvents) document.captureEvents(Event.MOUSEMOVE | Event.MOUSEUP | Event.MOUSEDOWN);
        this.started = false;
        this.startTime = new Date()
    },
    contextmenu: function ($) {
        if (this.context) BP(this.context, "contextmenu", this.contextmenu, this);
        BP(document, "contextmenu", this.contextmenu, this);
        $.preventDefault();
        $.stopPropagation()
    },
    move: function (_) {
        if (this.delay) if (new Date() - this.startTime < this.delay) return;
        if (!this.started) {
            this.started = true;
            this.onStart(this)
        }
        var $ = this;
        if (!this.timer) {
            $.now = [_.pageX, _.pageY];
            $.event = _;
            $.onMove($);
            $.timer = null
        }
    },
    stop: function (B) {
        this.now = [B.pageX, B.pageY];
        this.event = B;
        if (this.timer) {
            clearTimeout(this.timer);
            this.timer = null
        }
        var A = document;
        mini.selectable(this.trigger, true);
        mini.selectable(A.body, true);
        if (this.capture) if (isIE) this.trigger.releaseCapture();
        else if (document.captureEvents) document.releaseEvents(Event.MOUSEMOVE | Event.MOUSEUP | Event.MOUSEDOWN);
        var _ = mini.MouseButton.Right != B.button;
        if (_ == false) B.preventDefault();
        BP(A, "mousemove", this.move, this);
        BP(A, "mouseup", this.stop, this);
        var $ = this;
        setTimeout(function () {
            BP(document, "contextmenu", $.contextmenu, $);
            if ($.context) BP($.context, "contextmenu", $.contextmenu, $)
        },
		1);
        if (this.started) this.onStop(this, _)
    }
};
mini.JSON = new (function () {
    var sb = [],
	useHasOwn = !!{}.hasOwnProperty,
	replaceString = function ($, A) {
	    var _ = m[A];
	    if (_) return _;
	    _ = A.charCodeAt();
	    return "\\u00" + Math.floor(_ / 16).toString(16) + (_ % 16).toString(16)
	},
	doEncode = function ($) {
	    if ($ === null) {
	        sb[sb.length] = "null";
	        return
	    }
	    var A = typeof $;
	    if (A == "undefined") {
	        sb[sb.length] = "null";
	        return
	    } else if ($.push) {
	        sb[sb.length] = "[";
	        var D, _, C = $.length,
			E;
	        for (_ = 0; _ < C; _ += 1) {
	            E = $[_];
	            A = typeof E;
	            if (A == "undefined" || A == "function" || A == "unknown");
	            else {
	                if (D) sb[sb.length] = ",";
	                doEncode(E);
	                D = true
	            }
	        }
	        sb[sb.length] = "]";
	        return
	    } else if ($.getFullYear) {
	        var B;
	        sb[sb.length] = "\"";
	        sb[sb.length] = $.getFullYear();
	        sb[sb.length] = "-";
	        B = $.getMonth() + 1;
	        sb[sb.length] = B < 10 ? "0" + B : B;
	        sb[sb.length] = "-";
	        B = $.getDate();
	        sb[sb.length] = B < 10 ? "0" + B : B;
	        sb[sb.length] = "T";
	        B = $.getHours();
	        sb[sb.length] = B < 10 ? "0" + B : B;
	        sb[sb.length] = ":";
	        B = $.getMinutes();
	        sb[sb.length] = B < 10 ? "0" + B : B;
	        sb[sb.length] = ":";
	        B = $.getSeconds();
	        sb[sb.length] = B < 10 ? "0" + B : B;
	        sb[sb.length] = "\"";
	        return
	    } else if (A == "string") {
	        if (strReg1.test($)) {
	            sb[sb.length] = "\"";
	            sb[sb.length] = $.replace(strReg2, replaceString);
	            sb[sb.length] = "\"";
	            return
	        }
	        sb[sb.length] = "\"" + $ + "\"";
	        return
	    } else if (A == "number") {
	        sb[sb.length] = $;
	        return
	    } else if (A == "boolean") {
	        sb[sb.length] = String($);
	        return
	    } else {
	        sb[sb.length] = "{";
	        D,
			_,
			E;
	        for (_ in $) if (!useHasOwn || $.hasOwnProperty(_)) {
	            E = $[_];
	            A = typeof E;
	            if (A == "undefined" || A == "function" || A == "unknown");
	            else {
	                if (D) sb[sb.length] = ",";
	                doEncode(_);
	                sb[sb.length] = ":";
	                doEncode(E);
	                D = true
	            }
	        }
	        sb[sb.length] = "}";
	        return
	    }
	},
	m = {
	    "\b": "\\b",
	    "\t": "\\t",
	    "\n": "\\n",
	    "\f": "\\f",
	    "\r": "\\r",
	    "\"": "\\\"",
	    "\\": "\\\\"
	},
	strReg1 = /["\\\x00-\x1f]/,
	strReg2 = /([\x00-\x1f\\"])/g;
    this.encode = function () {
        var $;
        return function ($, _) {
            sb = [];
            doEncode($);
            return sb.join("")
        }
    } ();
    this.decode = function () {
        var re = /[\"\'](\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2}):(\d{2})[\"\']/g;
        return function (json) {
            if (json === "" || json === null || json === undefined) return json;
            json = json.replace(re, "new Date($1,$2-1,$3,$4,$5,$6)");
            var exp = json.replace(__js_dateRegEx, "$1new Date($2)"),
			s = eval("(" + json + ")");
            return s
        }
    } ()
})();
__js_dateRegEx = new RegExp("(^|[^\\\\])\\\"\\\\/Date\\((-?[0-9]+)(?:[a-zA-Z]|(?:\\+|-)[0-9]{4})?\\)\\\\/\\\"", "g");
mini.encode = mini.JSON.encode;
mini.decode = mini.JSON.decode;
mini.clone = function ($) {
    if ($ === null || $ === undefined) return $;
    var B = mini.encode($),
	_ = mini.decode(B);
    function A(B) {
        for (var _ = 0, D = B.length; _ < D; _++) {
            var $ = B[_];
            delete $._state;
            delete $._id;
            delete $._pid;
            for (var C in $) {
                var E = $[C];
                if (E instanceof Array) A(E)
            }
        }
    }
    A(_ instanceof Array ? _ : [_]);
    return _
};
var DAY_MS = 86400000,
HOUR_MS = 3600000,
MINUTE_MS = 60000;
mini.copyTo(mini, {
    clearTime: function ($) {
        if (!$) return null;
        return new Date($.getFullYear(), $.getMonth(), $.getDate())
    },
    maxTime: function ($) {
        if (!$) return null;
        return new Date($.getFullYear(), $.getMonth(), $.getDate(), 23, 59, 59)
    },
    cloneDate: function ($) {
        if (!$) return null;
        return new Date($.getTime())
    },
    addDate: function (A, $, _) {
        if (!_) _ = "D";
        A = new Date(A.getTime());
        switch (_.toUpperCase()) {
            case "Y":
                A.setFullYear(A.getFullYear() + $);
                break;
            case "MO":
                A.setMonth(A.getMonth() + $);
                break;
            case "D":
                A.setDate(A.getDate() + $);
                break;
            case "H":
                A.setHours(A.getHours() + $);
                break;
            case "M":
                A.setMinutes(A.getMinutes() + $);
                break;
            case "S":
                A.setSeconds(A.getSeconds() + $);
                break;
            case "MS":
                A.setMilliseconds(A.getMilliseconds() + $);
                break
        }
        return A
    },
    getWeek: function (D, $, _) {
        $ += 1;
        var E = Math.floor((14 - ($)) / 12),
		G = D + 4800 - E,
		A = ($) + (12 * E) - 3,
		C = _ + Math.floor(((153 * A) + 2) / 5) + (365 * G) + Math.floor(G / 4) - Math.floor(G / 100) + Math.floor(G / 400) - 32045,
		F = (C + 31741 - (C % 7)) % 146097 % 36524 % 1461,
		H = Math.floor(F / 1460),
		B = ((F - H) % 365) + H;
        NumberOfWeek = Math.floor(B / 7) + 1;
        return NumberOfWeek
    },
    getWeekStartDate: function (C, B) {
        if (!B) B = 0;
        if (B > 6 || B < 0) throw new Error("out of weekday");
        var A = C.getDay(),
		_ = B - A;
        if (A < B) _ -= 7;
        var $ = new Date(C.getFullYear(), C.getMonth(), C.getDate() + _);
        return $
    },
    getShortWeek: function (_) {
        var $ = this.dateInfo.daysShort;
        return $[_]
    },
    getLongWeek: function (_) {
        var $ = this.dateInfo.daysLong;
        return $[_]
    },
    getShortMonth: function ($) {
        var _ = this.dateInfo.monthsShort;
        return _[$]
    },
    getLongMonth: function ($) {
        var _ = this.dateInfo.monthsLong;
        return _[$]
    },
    dateInfo: {
        monthsLong: ["January", "Febraury", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
        monthsShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
        daysLong: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
        daysShort: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
        quarterLong: ["Q1", "Q2", "Q3", "Q4"],
        quarterShort: ["Q1", "Q2", "Q3", "Q4"],
        halfYearLong: ["first half", "second half"],
        patterns: {
            "d": "M/d/yyyy",
            "D": "dddd,MMMM dd,yyyy",
            "f": "dddd,MMMM dd,yyyy H:mm tt",
            "F": "dddd,MMMM dd,yyyy H:mm:ss tt",
            "g": "M/d/yyyy H:mm tt",
            "G": "M/d/yyyy H:mm:ss tt",
            "m": "MMMM dd",
            "o": "yyyy-MM-ddTHH:mm:ss.fff",
            "s": "yyyy-MM-ddTHH:mm:ss",
            "t": "H:mm tt",
            "T": "H:mm:ss tt",
            "U": "dddd,MMMM dd,yyyy HH:mm:ss tt",
            "y": "MMM,yyyy"
        },
        tt: {
            "AM": "AM",
            "PM": "PM"
        },
        ten: {
            "Early": "Early",
            "Mid": "Mid",
            "Late": "Late"
        },
        today: "Today",
        clockType: 24
    }
});
Date["prototype"].getHalfYear = function () {
    if (!this.getMonth) return null;
    var $ = this.getMonth();
    if ($ < 6) return 0;
    return 1
};
Date["prototype"].getQuarter = function () {
    if (!this.getMonth) return null;
    var $ = this.getMonth();
    if ($ < 3) return 0;
    if ($ < 6) return 1;
    if ($ < 9) return 2;
    return 3
};
mini.formatDate = function (C, O, F) {
    if (!C || !C.getFullYear || isNaN(C)) return "";
    var G = C.toString(),
	B = mini.dateInfo;
    if (!B) B = mini.dateInfo;
    if (typeof (B) !== "undefined") {
        var M = typeof (B.patterns[O]) !== "undefined" ? B.patterns[O] : O,
		J = C.getFullYear(),
		$ = C.getMonth(),
		_ = C.getDate();
        if (O == "yyyy-MM-dd") {
            $ = $ + 1 < 10 ? "0" + ($ + 1) : $ + 1;
            _ = _ < 10 ? "0" + _ : _;
            return J + "-" + $ + "-" + _
        }
        if (O == "MM/dd/yyyy") {
            $ = $ + 1 < 10 ? "0" + ($ + 1) : $ + 1;
            _ = _ < 10 ? "0" + _ : _;
            return $ + "/" + _ + "/" + J
        }
        G = M.replace(/yyyy/g, J);
        G = G.replace(/yy/g, (J + "").substring(2));
        var L = C.getHalfYear();
        G = G.replace(/hy/g, B.halfYearLong[L]);
        var I = C.getQuarter();
        G = G.replace(/Q/g, B.quarterLong[I]);
        G = G.replace(/q/g, B.quarterShort[I]);
        G = G.replace(/MMMM/g, B.monthsLong[$].escapeDateTimeTokens());
        G = G.replace(/MMM/g, B.monthsShort[$].escapeDateTimeTokens());
        G = G.replace(/MM/g, $ + 1 < 10 ? "0" + ($ + 1) : $ + 1);
        G = G.replace(/(\\)?M/g,
		function (A, _) {
		    return _ ? A : $ + 1
		});
        var N = C.getDay();
        G = G.replace(/dddd/g, B.daysLong[N].escapeDateTimeTokens());
        G = G.replace(/ddd/g, B.daysShort[N].escapeDateTimeTokens());
        G = G.replace(/dd/g, _ < 10 ? "0" + _ : _);
        G = G.replace(/(\\)?d/g,
		function (A, $) {
		    return $ ? A : _
		});
        var H = C.getHours(),
		A = H > 12 ? H - 12 : H;
        if (B.clockType == 12) if (H > 12) H -= 12;
        G = G.replace(/HH/g, H < 10 ? "0" + H : H);
        G = G.replace(/(\\)?H/g,
		function (_, $) {
		    return $ ? _ : H
		});
        G = G.replace(/hh/g, A < 10 ? "0" + A : A);
        G = G.replace(/(\\)?h/g,
		function (_, $) {
		    return $ ? _ : A
		});
        var D = C.getMinutes();
        G = G.replace(/mm/g, D < 10 ? "0" + D : D);
        G = G.replace(/(\\)?m/g,
		function (_, $) {
		    return $ ? _ : D
		});
        var K = C.getSeconds();
        G = G.replace(/ss/g, K < 10 ? "0" + K : K);
        G = G.replace(/(\\)?s/g,
		function (_, $) {
		    return $ ? _ : K
		});
        G = G.replace(/fff/g, C.getMilliseconds());
        G = G.replace(/tt/g, C.getHours() > 12 || C.getHours() == 0 ? B.tt["PM"] : B.tt["AM"]);
        var C = C.getDate(),
		E = "";
        if (C <= 10) E = B.ten["Early"];
        else if (C <= 20) E = B.ten["Mid"];
        else E = B.ten["Late"];
        G = G.replace(/ten/g, E)
    }
    return G.replace(/\\/g, "")
};
String["prototype"].escapeDateTimeTokens = function () {
    return this.replace(/([dMyHmsft])/g, "\\$1")
};
mini.fixDate = function ($, _) {
    if (+$) while ($.getDate() != _.getDate()) $.setTime(+$ + ($ < _ ? 1 : -1) * HOUR_MS)
};
mini.parseDate = function (s, ignoreTimezone) {
    try {
        var d = eval(s);
        if (d && d.getFullYear) return d
    } catch (ex) { }
    if (typeof s == "object") return isNaN(s) ? null : s;
    if (typeof s == "number") {
        d = new Date(s * 1000);
        if (d.getTime() != s) return null;
        return isNaN(d) ? null : d
    }
    if (typeof s == "string") {
        if (s.match(/^\d+(\.\d+)?$/)) {
            d = new Date(parseFloat(s) * 1000);
            if (d.getTime() != s) return null;
            else return d
        }
        if (ignoreTimezone === undefined) ignoreTimezone = true;
        d = mini.parseISO8601(s, ignoreTimezone) || (s ? new Date(s) : null);
        return isNaN(d) ? null : d
    }
    return null
};
mini.parseISO8601 = function (D, $) {
    var _ = D.match(/^([0-9]{4})([-\/]([0-9]{1,2})([-\/]([0-9]{1,2})([T ]([0-9]{1,2}):([0-9]{1,2})(:([0-9]{1,2})(\.([0-9]+))?)?(Z|(([-+])([0-9]{2})(:?([0-9]{2}))?))?)?)?)?$/);
    if (!_) {
        _ = D.match(/^([0-9]{4})[-\/]([0-9]{2})[-\/]([0-9]{2})[T ]([0-9]{1,2})/);
        if (_) {
            var A = new Date(_[1], _[2] - 1, _[3], _[4]);
            return A
        }
        _ = D.match(/^([0-9]{2})-([0-9]{2})-([0-9]{4})$/);
        if (!_) return null;
        else {
            A = new Date(_[3], _[1] - 1, _[2]);
            return A
        }
    }
    A = new Date(_[1], 0, 1);
    if ($ || !_[14]) {
        var C = new Date(_[1], 0, 1, 9, 0);
        if (_[3]) {
            A.setMonth(_[3] - 1);
            C.setMonth(_[3] - 1)
        }
        if (_[5]) {
            A.setDate(_[5]);
            C.setDate(_[5])
        }
        mini.fixDate(A, C);
        if (_[7]) A.setHours(_[7]);
        if (_[8]) A.setMinutes(_[8]);
        if (_[10]) A.setSeconds(_[10]);
        if (_[12]) A.setMilliseconds(Number("0." + _[12]) * 1000);
        mini.fixDate(A, C)
    } else {
        A.setUTCFullYear(_[1], _[3] ? _[3] - 1 : 0, _[5] || 1);
        A.setUTCHours(_[7] || 0, _[8] || 0, _[10] || 0, _[12] ? Number("0." + _[12]) * 1000 : 0);
        var B = Number(_[16]) * 60 + (_[18] ? Number(_[18]) : 0);
        B *= _[15] == "-" ? 1 : -1;
        A = new Date(+A + (B * 60 * 1000))
    }
    return A
};
mini.parseTime = function (E, F) {
    if (!E) return null;
    var B = parseInt(E);
    if (B == E && F) {
        $ = new Date(0);
        if (F[0] == "H") $.setHours(B);
        else if (F[0] == "m") $.setMinutes(B);
        else if (F[0] == "s") $.setSeconds(B);
        return $
    }
    var $ = mini.parseDate(E);
    if (!$) {
        var D = E.split(":"),
		_ = parseInt(parseFloat(D[0])),
		C = parseInt(parseFloat(D[1])),
		A = parseInt(parseFloat(D[2]));
        if (!isNaN(_) && !isNaN(C) && !isNaN(A)) {
            $ = new Date(0);
            $.setHours(_);
            $.setMinutes(C);
            $.setSeconds(A)
        }
        if (!isNaN(_) && (F == "H" || F == "HH")) {
            $ = new Date(0);
            $.setHours(_)
        } else if (!isNaN(_) && !isNaN(C) && (F == "H:mm" || F == "HH:mm")) {
            $ = new Date(0);
            $.setHours(_);
            $.setMinutes(C)
        } else if (!isNaN(_) && !isNaN(C) && F == "mm:ss") {
            $ = new Date(0);
            $.setMinutes(_);
            $.setSeconds(C)
        }
    }
    return $
};
mini.dateInfo = {
    monthsLong: ["\u4e00\u6708", "\u4e8c\u6708", "\u4e09\u6708", "\u56db\u6708", "\u4e94\u6708", "\u516d\u6708", "\u4e03\u6708", "\u516b\u6708", "\u4e5d\u6708", "\u5341\u6708", "\u5341\u4e00\u6708", "\u5341\u4e8c\u6708"],
    monthsShort: ["1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708"],
    daysLong: ["\u661f\u671f\u65e5", "\u661f\u671f\u4e00", "\u661f\u671f\u4e8c", "\u661f\u671f\u4e09", "\u661f\u671f\u56db", "\u661f\u671f\u4e94", "\u661f\u671f\u516d"],
    daysShort: ["\u65e5", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d"],
    quarterLong: ["\u4e00\u5b63\u5ea6", "\u4e8c\u5b63\u5ea6", "\u4e09\u5b63\u5ea6", "\u56db\u5b63\u5ea6"],
    quarterShort: ["Q1", "Q2", "Q2", "Q4"],
    halfYearLong: ["\u4e0a\u534a\u5e74", "\u4e0b\u534a\u5e74"],
    patterns: {
        "d": "yyyy-M-d",
        "D": "yyyy\u5e74M\u6708d\u65e5",
        "f": "yyyy\u5e74M\u6708d\u65e5 H:mm",
        "F": "yyyy\u5e74M\u6708d\u65e5 H:mm:ss",
        "g": "yyyy-M-d H:mm",
        "G": "yyyy-M-d H:mm:ss",
        "m": "MMMd\u65e5",
        "o": "yyyy-MM-ddTHH:mm:ss.fff",
        "s": "yyyy-MM-ddTHH:mm:ss",
        "t": "H:mm",
        "T": "H:mm:ss",
        "U": "yyyy\u5e74M\u6708d\u65e5 HH:mm:ss",
        "y": "yyyy\u5e74MM\u6708"
    },
    tt: {
        "AM": "\u4e0a\u5348",
        "PM": "\u4e0b\u5348"
    },
    ten: {
        "Early": "\u4e0a\u65ec",
        "Mid": "\u4e2d\u65ec",
        "Late": "\u4e0b\u65ec"
    },
    today: "\u4eca\u5929",
    clockType: 24
};
E1R = function ($) {
    if (typeof $ == "string") {
        if ($.charAt(0) == "#") $ = $.substr(1);
        return document.getElementById($)
    } else return $
};
D4ge = function ($, _) {
    $ = E1R($);
    if (!$) return;
    if (!$.className) return;
    var A = $.className.split(" ");
    return A.indexOf(_) != -1
};
RE = function ($, _) {
    if (!_) return;
    if (D4ge($, _) == false) jQuery($)["addClass"](_)
};
WeL = function ($, _) {
    if (!_) return;
    jQuery($)["removeClass"](_)
};
OFY = function ($) {
    $ = E1R($);
    var _ = jQuery($);
    return {
        top: parseInt(_.css("margin-top"), 10) || 0,
        left: parseInt(_.css("margin-left"), 10) || 0,
        bottom: parseInt(_.css("margin-bottom"), 10) || 0,
        right: parseInt(_.css("margin-right"), 10) || 0
    }
};
Ly5 = function ($) {
    $ = E1R($);
    var _ = jQuery($);
    return {
        top: parseInt(_.css("border-top-width"), 10) || 0,
        left: parseInt(_.css("border-left-width"), 10) || 0,
        bottom: parseInt(_.css("border-bottom-width"), 10) || 0,
        right: parseInt(_.css("border-right-width"), 10) || 0
    }
};
M32 = function ($) {
    $ = E1R($);
    var _ = jQuery($);
    return {
        top: parseInt(_.css("padding-top"), 10) || 0,
        left: parseInt(_.css("padding-left"), 10) || 0,
        bottom: parseInt(_.css("padding-bottom"), 10) || 0,
        right: parseInt(_.css("padding-right"), 10) || 0
    }
};
SI9N = function (_, $) {
    _ = E1R(_);
    $ = parseInt($);
    if (isNaN($) || !_) return;
    if (jQuery.boxModel) {
        var A = M32(_),
		B = Ly5(_);
        $ = $ - A.left - A.right - B.left - B.right
    }
    if ($ < 0) $ = 0;
    _.style.width = $ + "px"
};
Quj1 = function (_, $) {
    _ = E1R(_);
    $ = parseInt($);
    if (isNaN($) || !_) return;
    if (jQuery.boxModel) {
        var A = M32(_),
		B = Ly5(_);
        $ = $ - A.top - A.bottom - B.top - B.bottom
    }
    if ($ < 0) $ = 0;
    _.style.height = $ + "px"
};
L_h = function ($, _) {
    $ = E1R($);
    if ($.style.display == "none" || $.type == "text/javascript") return 0;
    return _ ? jQuery($).width() : jQuery($).outerWidth()
};
Nf = function ($, _) {
    $ = E1R($);
    if ($.style.display == "none" || $.type == "text/javascript") return 0;
    return _ ? jQuery($).height() : jQuery($).outerHeight()
};
Ggi = function (A, C, B, $, _) {
    if (B === undefined) {
        B = C.y;
        $ = C.width;
        _ = C.height;
        C = C.x
    }
    mini["setXY"](A, C, B);
    SI9N(A, $);
    Quj1(A, _)
};
EcG = function (A) {
    var $ = mini.getXY(A),
	_ = {
	    x: $[0],
	    y: $[1],
	    width: L_h(A),
	    height: Nf(A)
	};
    _.left = _.x;
    _.top = _.y;
    _.right = _.x + _.width;
    _.bottom = _.y + _.height;
    return _
};
BIT = function (A, B) {
    A = E1R(A);
    if (!A || typeof B != "string") return;
    var F = jQuery(A),
	_ = B.toLowerCase().split(";");
    for (var $ = 0, C = _.length; $ < C; $++) {
        var E = _[$],
		D = E.split(":");
        if (D.length == 2) F.css(D[0].trim(), D[1].trim())
    }
};
YI = function () {
    var $ = document.defaultView;
    return new Function("el", "style", ["style.indexOf('-')>-1 && (style=style.replace(/-(\\w)/g,function(m,a){return a.toUpperCase()}));", "style=='float' && (style='", $ ? "cssFloat" : "styleFloat", "');return el.style[style] || ", $ ? "window.getComputedStyle(el,null)[style]" : "el.currentStyle[style]", " || null;"].join(""))
} ();
Yma = function (A, $) {
    var _ = false;
    A = E1R(A);
    $ = E1R($);
    if (A === $) return true;
    if (A && $) if (A.contains) {
        try {
            return A.contains($)
        } catch (B) {
            return false
        }
    } else if (A.compareDocumentPosition) return !!(A.compareDocumentPosition($) & 16);
    else while ($ = $.parentNode) _ = $ == A || _;
    return _
};
MRj9 = function (B, A, $) {
    B = E1R(B);
    var C = document.body,
	_ = 0,
	D;
    $ = $ || 50;
    if (typeof $ != "number") {
        D = E1R($);
        $ = 10
    }
    while (B && B.nodeType == 1 && _ < $ && B != C && B != D) {
        if (D4ge(B, A)) return B;
        _++;
        B = B.parentNode
    }
    return null
};
mini.copyTo(mini, {
    byId: E1R,
    hasClass: D4ge,
    addClass: RE,
    removeClass: WeL,
    getMargins: OFY,
    getBorders: Ly5,
    getPaddings: M32,
    setWidth: SI9N,
    setHeight: Quj1,
    getWidth: L_h,
    getHeight: Nf,
    setBox: Ggi,
    getBox: EcG,
    setStyle: BIT,
    getStyle: YI,
    repaint: function ($) {
        if (!$) $ = document.body;
        RE($, "mini-repaint");
        setTimeout(function () {
            WeL($, "mini-repaint")
        },
		1)
    },
    getSize: function ($, _) {
        return {
            width: L_h($, _),
            height: Nf($, _)
        }
    },
    setSize: function (A, $, _) {
        SI9N(A, $);
        Quj1(A, _)
    },
    setX: function (_, B) {
        B = parseInt(B);
        var $ = jQuery(_).offset(),
		A = parseInt($.top);
        if (A === undefined) A = $[1];
        mini["setXY"](_, B, A)
    },
    setY: function (_, A) {
        A = parseInt(A);
        var $ = jQuery(_).offset(),
		B = parseInt($.left);
        if (B === undefined) B = $[0];
        mini["setXY"](_, B, A)
    },
    setXY: function (_, B, A) {
        var $ = {
            left: parseInt(B),
            top: parseInt(A)
        };
        jQuery(_).offset($);
        jQuery(_).offset($)
    },
    getXY: function (_) {
        var $ = jQuery(_).offset();
        return [parseInt($.left), parseInt($.top)]
    },
    getViewportBox: function () {
        var $ = jQuery(window).width(),
		_ = jQuery(window).height(),
		B = jQuery(document).scrollLeft(),
		A = jQuery(document.body).scrollTop();
        if (document.documentElement) A = document.documentElement.scrollTop;
        return {
            x: B,
            y: A,
            width: $,
            height: _,
            right: B + $,
            bottom: A + _
        }
    },
    getChildNodes: function (A, C) {
        A = E1R(A);
        if (!A) return;
        var E = A.childNodes,
		B = [];
        for (var $ = 0, D = E.length; $ < D; $++) {
            var _ = E[$];
            if (_.nodeType == 1 || C === true) B.push(_)
        }
        return B
    },
    removeChilds: function (B, _) {
        B = E1R(B);
        if (!B) return;
        var C = mini["getChildNodes"](B, true);
        for (var $ = 0, D = C.length; $ < D; $++) {
            var A = C[$];
            if (_ && A == _);
            else B.removeChild(C[$])
        }
    },
    isAncestor: Yma,
    findParent: MRj9,
    findChild: function (_, A) {
        _ = E1R(_);
        var B = _.getElementsByTagName("*");
        for (var $ = 0, C = B.length; $ < C; $++) {
            var _ = B[$];
            if (D4ge(_, A)) return _
        }
    },
    isAncestor: function (A, $) {
        var _ = false;
        A = E1R(A);
        $ = E1R($);
        if (A === $) return true;
        if (A && $) if (A.contains) {
            try {
                return A.contains($)
            } catch (B) {
                return false
            }
        } else if (A.compareDocumentPosition) return !!(A.compareDocumentPosition($) & 16);
        else while ($ = $.parentNode) _ = $ == A || _;
        return _
    },
    getOffsetsTo: function (_, A) {
        var $ = this.getXY(_),
		B = this.getXY(A);
        return [$[0] - B[0], $[1] - B[1]]
    },
    scrollIntoView: function (I, H, F) {
        var B = E1R(H) || document.body,
		$ = this.getOffsetsTo(I, B),
		C = $[0] + B.scrollLeft,
		J = $[1] + B.scrollTop,
		D = J + I.offsetHeight,
		A = C + I.offsetWidth,
		G = B.clientHeight,
		K = parseInt(B.scrollTop, 10),
		_ = parseInt(B.scrollLeft, 10),
		L = K + G,
		E = _ + B.clientWidth;
        if (I.offsetHeight > G || J < K) B.scrollTop = J;
        else if (D > L) B.scrollTop = D - G;
        B.scrollTop = B.scrollTop;
        if (F !== false) {
            if (I.offsetWidth > B.clientWidth || C < _) B.scrollLeft = C;
            else if (A > E) B.scrollLeft = A - B.clientWidth;
            B.scrollLeft = B.scrollLeft
        }
        return this
    },
    setOpacity: function (_, $) {
        jQuery(_).css({
            "opacity": $
        })
    },
    selectable: function (_, $) {
        _ = E1R(_);
        if (!!$) {
            jQuery(_)["removeClass"]("mini-unselectable");
            if (isIE) _.unselectable = "off";
            else {
                _.style.MozUserSelect = "";
                _.style.KhtmlUserSelect = "";
                _.style.UserSelect = ""
            }
        } else {
            jQuery(_)["addClass"]("mini-unselectable");
            if (isIE) _.unselectable = "on";
            else {
                _.style.MozUserSelect = "none";
                _.style.UserSelect = "none";
                _.style.KhtmlUserSelect = "none"
            }
        }
    },
    selectRange: function (B, A, _) {
        if (B.createTextRange) {
            var $ = B.createTextRange();
            $.moveStart("character", A);
            $.moveEnd("character", _ - B.value.length);
            $["select"]()
        } else if (B.setSelectionRange) B.setSelectionRange(A, _);
        try {
            B.focus()
        } catch (C) { }
    },
    getSelectRange: function (A) {
        A = E1R(A);
        if (!A) return;
        try {
            A.focus()
        } catch (C) { }
        var $ = 0,
		B = 0;
        if (A.createTextRange) {
            var _ = document.selection.createRange().duplicate();
            _.moveEnd("character", A.value.length);
            if (_.text === "") $ = A.value.length;
            else $ = A.value.lastIndexOf(_.text);
            _ = document.selection.createRange().duplicate();
            _.moveStart("character", -A.value.length);
            B = _.text.length
        } else {
            $ = A.selectionStart;
            B = A.selectionEnd
        }
        return [$, B]
    }
});
(function () {
    var $ = {
        tabindex: "tabIndex",
        readonly: "readOnly",
        "for": "htmlFor",
        "class": "className",
        maxlength: "maxLength",
        cellspacing: "cellSpacing",
        cellpadding: "cellPadding",
        rowspan: "rowSpan",
        colspan: "colSpan",
        usemap: "useMap",
        frameborder: "frameBorder",
        contenteditable: "contentEditable"
    },
	_ = document.createElement("div");
    _.setAttribute("class", "t");
    var A = _.className === "t";
    mini.setAttr = function (B, C, _) {
        B.setAttribute(A ? C : ($[C] || C), _)
    };
    mini.getAttr = function (B, C) {
        if (C == "value" && (isIE6 || isIE7)) {
            var _ = B.attributes[C];
            return _ ? _.value : null
        }
        var D = B.getAttribute(A ? C : ($[C] || C));
        if (typeof D == "function") D = B.attributes[C].value;
        return D
    }
})();
_pS = function (_, $, C, A) {
    var B = "on" + $.toLowerCase();
    _[B] = function (_) {
        _ = _ || window.event;
        _.target = _.target || _.srcElement;
        if (!_.preventDefault) _.preventDefault = function () {
            var $ = this;
            if ($.preventDefault) $.preventDefault();
            else if (window.event) window.event.returnValue = false
        };
        if (!_.stopPropogation) _.stopPropogation = function () {
            var $ = this;
            if ($.stopPropagation) $.stopPropagation();
            else if (window.event) window.event.cancelBubble = true
        };
        var $ = C["call"](A, _);
        if ($ === false) return false
    }
};
VNV = function (_, $, D, A) {
    _ = E1R(_);
    A = A || _;
    if (!_ || !$ || !D || !A) return false;
    var B = mini["findListener"](_, $, D, A);
    if (B) return false;
    var C = mini.createDelegate(D, A);
    mini.listeners.push([_, $, D, A, C]);
    if (jQuery.browser.mozilla && $ == "mousewheel") $ = "DOMMouseScroll";
    jQuery(_).bind($, C)
};
BP = function (_, $, C, A) {
    _ = E1R(_);
    A = A || _;
    if (!_ || !$ || !C || !A) return false;
    var B = mini["findListener"](_, $, C, A);
    if (!B) return false;
    mini.listeners.remove(B);
    if (jQuery.browser.mozilla && $ == "mousewheel") $ = "DOMMouseScroll";
    jQuery(_).unbind($, B[4])
};
mini.copyTo(mini, {
    listeners: [],
    on: VNV,
    un: BP,
    findListener: function (A, _, F, B) {
        A = E1R(A);
        B = B || A;
        if (!A || !_ || !F || !B) return false;
        var D = mini.listeners;
        for (var $ = 0, E = D.length; $ < E; $++) {
            var C = D[$];
            if (C[0] == A && C[1] == _ && C[2] == F && C[3] == B) return C
        }
    },
    clearEvent: function (A, _) {
        A = E1R(A);
        if (!A) return false;
        var C = mini.listeners;
        for (var $ = C.length - 1; $ >= 0; $--) {
            var B = C[$];
            if (B[0] == A) if (!_ || _ == B[1]) BP(A, B[1], B[2], B[3])
        }
    }
});
mini.__windowResizes = [];
mini.onWindowResize = function (_, $) {
    mini.__windowResizes.push([_, $])
};
VNV(window, "resize",
function (C) {
    var _ = mini.__windowResizes;
    for (var $ = 0, B = _.length; $ < B; $++) {
        var A = _[$];
        A[0]["call"](A[1], C)
    }
});
mini.copyTo(Array.prototype, {
    add: Array["prototype"].enqueue = function ($) {
        this[this.length] = $;
        return this
    },
    getRange: function (_, A) {
        var B = [];
        for (var $ = _; $ <= A; $++) B[B.length] = this[$];
        return B
    },
    addRange: function (A) {
        for (var $ = 0, _ = A.length; $ < _; $++) this[this.length] = A[$];
        return this
    },
    clear: function () {
        this.length = 0;
        return this
    },
    clone: function () {
        if (this.length === 1) return [this[0]];
        else return Array.apply(null, this)
    },
    contains: function ($) {
        return (this.indexOf($) >= 0)
    },
    indexOf: function (_, B) {
        var $ = this.length;
        for (var A = (B < 0) ? Math.max(0, $ + B) : B || 0; A < $; A++) if (this[A] === _) return A;
        return -1
    },
    dequeue: function () {
        return this.shift()
    },
    insert: function (_, $) {
        this.splice(_, 0, $);
        return this
    },
    insertRange: function (_, B) {
        for (var A = B.length - 1; A >= 0; A--) {
            var $ = B[A];
            this.splice(_, 0, $)
        }
        return this
    },
    remove: function (_) {
        var $ = this.indexOf(_);
        if ($ >= 0) this.splice($, 1);
        return ($ >= 0)
    },
    removeAt: function ($) {
        var _ = this[$];
        this.splice($, 1);
        return _
    },
    removeRange: function (_) {
        _ = _.clone();
        for (var $ = 0, A = _.length; $ < A; $++) this.remove(_[$])
    }
});
mini.Keyboard = {
    Left: 37,
    Top: 38,
    Right: 39,
    Bottom: 40,
    PageUp: 33,
    PageDown: 34,
    End: 35,
    Home: 36,
    Enter: 13,
    ESC: 27,
    Space: 32,
    Tab: 9,
    Del: 46,
    F1: 112,
    F2: 113,
    F3: 114,
    F4: 115,
    F5: 116,
    F6: 117,
    F7: 118,
    F8: 119,
    F9: 120,
    F10: 121,
    F11: 122,
    F12: 123
};
var ua = navigator.userAgent.toLowerCase(),
check = function ($) {
    return $.test(ua)
},
DOC = document,
isStrict = DOC.compatMode == "CSS1Compat",
isOpera = Object["prototype"].toString["call"](window.opera) == "[object Opera]",
isChrome = check(/chrome/),
isWebKit = check(/webkit/),
isSafari = !isChrome && check(/safari/),
isSafari2 = isSafari && check(/applewebkit\/4/),
isSafari3 = isSafari && check(/version\/3/),
isSafari4 = isSafari && check(/version\/4/),
isIE = !!window.attachEvent && !isOpera,
isIE7 = isIE && check(/msie 7/),
isIE8 = isIE && check(/msie 8/),
isIE9 = isIE && check(/msie 9/),
isIE10 = isIE && document.documentMode == 10,
isIE6 = isIE && !isIE7 && !isIE8 && !isIE9 && !isIE10,
isFirefox = navigator.userAgent.indexOf("Firefox") > 0,
isGecko = !isWebKit && check(/gecko/),
isGecko2 = isGecko && check(/rv:1\.8/),
isGecko3 = isGecko && check(/rv:1\.9/),
isBorderBox = isIE && !isStrict,
isWindows = check(/windows|win32/),
isMac = check(/macintosh|mac os x/),
isAir = check(/adobeair/),
isLinux = check(/linux/),
isSecure = /^https/i.test(window.location.protocol);
if (isIE6) {
    try {
        DOC.execCommand("BackgroundImageCache", false, true)
    } catch (e) { }
}
mini.isIE = isIE;
mini.isIE6 = isIE6;
mini.isIE7 = isIE7;
mini.isIE8 = isIE8;
mini.isIE9 = isIE9;
mini.isFireFox = jQuery.browser.mozilla;
mini.isOpera = jQuery.browser.opera;
mini.isSafari = jQuery.browser.safari;
mini.noBorderBox = false;
if (jQuery.boxModel == false && isIE && isIE9 == false) mini.noBorderBox = true;
mini.MouseButton = {
    Left: 0,
    Middle: 1,
    Right: 2
};
if (isIE && !isIE9) mini.MouseButton = {
    Left: 1,
    Middle: 4,
    Right: 2
};
mini._MaskID = 1;
mini._MaskObjects = {};
mini.mask = function (C) {
    var _ = E1R(C);
    if (mini.isElement(_)) C = {
        el: _
    };
    else if (typeof C == "string") C = {
        html: C
    };
    C = mini.copyTo({
        html: "",
        cls: "",
        style: "",
        backStyle: "background:#ccc"
    },
	C);
    C.el = E1R(C.el);
    if (!C.el) C.el = document.body;
    _ = C.el;
    mini["unmask"](C.el);
    _._maskid = mini._MaskID++;
    mini._MaskObjects[_._maskid] = C;
    var $ = mini.append(_, "<div class=\"mini-mask\">" + "<div class=\"mini-mask-background\" style=\"" + C.backStyle + "\"></div>" + "<div class=\"mini-mask-msg " + C.cls + "\" style=\"" + C.style + "\">" + C.html + "</div>" + "</div>");
    C.maskEl = $;
    if (!mini.isNull(C.opacity)) mini.setOpacity($.firstChild, C.opacity);
    function A() {
        B.style.display = "block";
        var $ = mini.getSize(B);
        B.style.marginLeft = -$.width / 2 + "px";
        B.style.marginTop = -$.height / 2 + "px"
    }
    var B = $.lastChild;
    B.style.display = "none";
    setTimeout(function () {
        A()
    },
	0)
};
mini["unmask"] = function (_) {
    _ = E1R(_);
    if (!_) _ = document.body;
    var A = mini._MaskObjects[_._maskid];
    if (!A) return;
    delete mini._MaskObjects[_._maskid];
    var $ = A.maskEl;
    A.maskEl = null;
    if ($ && $.parentNode) $.parentNode.removeChild($)
};
mini.Cookie = {
    get: function (D) {
        var A = document.cookie.split("; "),
		B = null;
        for (var $ = 0; $ < A.length; $++) {
            var _ = A[$].split("=");
            if (D == _[0]) B = _
        }
        if (B) {
            var C = B[1];
            if (C === undefined) return C;
            return unescape(C)
        }
        return null
    },
    set: function (C, $, B, A) {
        var _ = new Date();
        if (B != null) _ = new Date(_.getTime() + (B * 1000 * 3600 * 24));
        document.cookie = C + "=" + escape($) + ((B == null) ? "" : ("; expires=" + _.toGMTString())) + ";path=/" + (A ? "; domain=" + A : "")
    },
    del: function (_, $) {
        this.set(_, null, -100, $)
    }
};
mini.copyTo(mini, {
    treeToArray: function (C, I, J, A, $) {
        if (!I) I = "children";
        var F = [];
        for (var H = 0, D = C.length; H < D; H++) {
            var B = C[H];
            F[F.length] = B;
            if (A) B[A] = $;
            var _ = B[I];
            if (_ && _.length > 0) {
                var E = B[J],
				G = this["treeToArray"](_, I, J, A, E);
                F.addRange(G)
            }
        }
        return F
    },
    arrayToTree: function (C, A, H, B) {
        if (!A) A = "children";
        H = H || "_id";
        B = B || "_pid";
        var G = [],
		F = {};
        for (var _ = 0, E = C.length; _ < E; _++) {
            var $ = C[_],
			I = $[H];
            if (I !== null && I !== undefined) F[I] = $;
            delete $[A]
        }
        for (_ = 0, E = C.length; _ < E; _++) {
            var $ = C[_],
			D = F[$[B]];
            if (!D) {
                G.push($);
                continue
            }
            if (!D[A]) D[A] = [];
            D[A].push($)
        }
        return G
    }
});
function UUID() {
    var A = [],
	_ = "0123456789ABCDEF".split("");
    for (var $ = 0; $ < 36; $++) A[$] = Math.floor(Math.random() * 16);
    A[14] = 4;
    A[19] = (A[19] & 3) | 8;
    for ($ = 0; $ < 36; $++) A[$] = _[A[$]];
    A[8] = A[13] = A[18] = A[23] = "-";
    return A.join("")
}
String.format = function (_) {
    var $ = Array["prototype"].slice["call"](arguments, 1);
    _ = _ || "";
    return _.replace(/\{(\d+)\}/g,
	function (A, _) {
	    return $[_]
	})
};
String["prototype"].trim = function () {
    var $ = /^\s+|\s+$/g;
    return function () {
        return this.replace($, "")
    }
} ();
mini.copyTo(mini, {
    measureText: function (B, _, C) {
        if (!this.measureEl) this.measureEl = mini.append(document.body, "<div></div>");
        this.measureEl.style.cssText = "position:absolute;left:-1000px;top:-1000px;visibility:hidden;";
        if (typeof B == "string") this.measureEl.className = B;
        else {
            this.measureEl.className = "";
            var G = jQuery(B),
			A = jQuery(this.measureEl),
			F = ["font-size", "font-style", "font-weight", "font-family", "line-height", "text-transform", "letter-spacing"];
            for (var $ = 0, E = F.length; $ < E; $++) {
                var D = F[$];
                A.css(D, G.css(D))
            }
        }
        if (C) BIT(this.measureEl, C);
        this.measureEl.innerHTML = _;
        return mini.getSize(this.measureEl)
    }
});
jQuery(function () {
    var $ = new Date();
    mini.isReady = true;
    mini.parse();
    JXG();
    if ((YI(document.body, "overflow") == "hidden" || YI(document.documentElement, "overflow") == "hidden") && (isIE6 || isIE7)) {
        jQuery(document.body).css("overflow", "visible");
        jQuery(document.documentElement).css("overflow", "visible")
    }
    mini.__LastWindowWidth = document.documentElement.clientWidth;
    mini.__LastWindowHeight = document.documentElement.clientHeight
});
mini_onload = function ($) {
    mini.layout(null, false);
    VNV(window, "resize", mini_onresize)
};
VNV(window, "load", mini_onload);
mini.__LastWindowWidth = document.documentElement.clientWidth;
mini.__LastWindowHeight = document.documentElement.clientHeight;
mini.doWindowResizeTimer = null;
mini.allowLayout = true;
mini_onresize = function ($) {
    if (mini.doWindowResizeTimer) clearTimeout(mini.doWindowResizeTimer);
    if (NTp == false || mini.allowLayout == false) return;
    if (typeof Ext != "undefined") mini.doWindowResizeTimer = setTimeout(function () {
        var _ = document.documentElement.clientWidth,
		$ = document.documentElement.clientHeight;
        if (mini.__LastWindowWidth == _ && mini.__LastWindowHeight == $);
        else {
            mini.__LastWindowWidth = _;
            mini.__LastWindowHeight = $;
            mini.layout(null, false)
        }
        mini.doWindowResizeTimer = null
    },
	300);
    else mini.doWindowResizeTimer = setTimeout(function () {
        var _ = document.documentElement.clientWidth,
		$ = document.documentElement.clientHeight;
        if (mini.__LastWindowWidth == _ && mini.__LastWindowHeight == $);
        else {
            mini.__LastWindowWidth = _;
            mini.__LastWindowHeight = $;
            mini.layout(null, false)
        }
        mini.doWindowResizeTimer = null
    },
	100)
};
mini["isDisplay"] = function (_, A) {
    var $ = A || document.body;
    while (1) {
        if (_ == null || !_.style) return false;
        if (_ && _.style && _.style.display == "none") return false;
        if (_ == $) return true;
        _ = _.parentNode
    }
    return true
};
mini.isWindowDisplay = function () {
    try {
        var _ = window.parent,
		E = _ != window;
        if (E) {
            var C = _.document.getElementsByTagName("iframe"),
			H = _.document.getElementsByTagName("frame"),
			G = [];
            for (var $ = 0, D = C.length; $ < D; $++) G.push(C[$]);
            for ($ = 0, D = H.length; $ < D; $++) G.push(H[$]);
            var B = null;
            for ($ = 0, D = G.length; $ < D; $++) {
                var A = G[$];
                if (A.contentWindow == window) {
                    B = A;
                    break
                }
            }
            if (!B) return false;
            return mini["isDisplay"](B, _.document.body)
        } else return true
    } catch (F) {
        return true
    }
};
NTp = mini.isWindowDisplay();
mini.layoutIFrames = function ($) {
    if (!$) $ = document.body;
    var _ = $.getElementsByTagName("iframe");
    setTimeout(function () {
        for (var A = 0, C = _.length; A < C; A++) {
            var B = _[A];
            try {
                if (mini["isDisplay"](B) && Yma($, B)) {
                    if (B.contentWindow.mini) if (B.contentWindow.NTp == false) {
                        B.contentWindow.NTp = B.contentWindow.mini.isWindowDisplay();
                        B.contentWindow.mini.layout()
                    } else B.contentWindow.mini.layout(null, false);
                    B.contentWindow.mini.layoutIFrames()
                }
            } catch (D) { }
        }
    },
	30)
};
$.ajaxSetup({
    cache: false
});
if (isIE) setInterval(function () {
    CollectGarbage()
},
1000);
mini_unload = function (F) {
    var E = document.body.getElementsByTagName("iframe");
    if (E.length > 0) {
        var D = [];
        for (var $ = 0, C = E.length; $ < C; $++) D.push(E[$]);
        for ($ = 0, C = D.length; $ < C; $++) {
            try {
                var B = D[$];
                B.src = "";
                if (B.parentNode) B.parentNode.removeChild(B)
            } catch (F) { }
        }
    }
    var A = mini.getComponents();
    for ($ = 0, C = A.length; $ < C; $++) {
        var _ = A[$];
        _["destroy"](false)
    }
    A.length = 0;
    A = null;
    BP(window, "unload", mini_unload);
    BP(window, "load", mini_onload);
    BP(window, "resize", mini_onresize);
    mini.components = {};
    mini.classes = {};
    mini.uiClasses = {};
    try {
        CollectGarbage()
    } catch (F) { }
};
VNV(window, "unload", mini_unload);
function __OnIFrameMouseDown() {
    jQuery(document).trigger("mousedown")
}
function __BindIFrames() {
    var C = document.getElementsByTagName("iframe");
    for (var $ = 0, A = C.length; $ < A; $++) {
        var _ = C[$];
        try {
            if (_.contentWindow) _.contentWindow.document.onmousedown = __OnIFrameMouseDown
        } catch (B) { }
    }
}
setInterval(function () {
    __BindIFrames()
},
1500);
mini.zIndex = 1000;
mini.getMaxZIndex = function () {
    return mini.zIndex++
};
mini.DataBinding = function () {
    this._bindFields = [];
    this._bindForms = [];
    mini.DataBinding["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.DataBinding, mini.Component, {
    bindField: function (A, D, C, B, $) {
        A = mini.get(A);
        D = mini.get(D);
        if (!A || !D || !C) return;
        var _ = {
            control: A,
            source: D,
            field: C,
            convert: $,
            mode: B
        };
        this._bindFields.push(_);
        D.on("currentchanged", this.BPCR, this);
        A.on("valuechanged", this.NWj, this)
    },
    bindForm: function (B, F, D, A) {
        B = E1R(B);
        F = mini.get(F);
        if (!B || !F) return;
        var B = new mini.Form(B),
		$ = B.getFields();
        for (var _ = 0, E = $.length; _ < E; _++) {
            var C = $[_];
            this.bindField(C, F, C.getName(), D, A)
        }
    },
    BPCR: function (H) {
        if (this._doSetting) return;
        this._doSetting = true;
        var G = H.sender,
		_ = H.record;
        for (var $ = 0, F = this._bindFields.length; $ < F; $++) {
            var B = this._bindFields[$];
            if (B.source != G) continue;
            var C = B.control,
			D = B.field;
            if (C["setValue"]) if (_) {
                var A = _[D];
                C["setValue"](A)
            } else C["setValue"]("");
            if (C["setText"] && C.textName) if (_) C["setText"](_[C.textName]);
            else C["setText"]("")
        }
        var E = this;
        setTimeout(function () {
            E._doSetting = false
        },
		10)
    },
    NWj: function (H) {
        if (this._doSetting) return;
        this._doSetting = true;
        var D = H.sender,
		_ = D.getValue();
        for (var $ = 0, G = this._bindFields.length; $ < G; $++) {
            var C = this._bindFields[$];
            if (C.control != D || C.mode === false) continue;
            var F = C.source,
			B = F.getCurrent();
            if (!B) continue;
            var A = {};
            A[C.field] = _;
            if (D.getText && D.textName) A[D.textName] = D.getText();
            F["updateRow"](B, A)
        }
        var E = this;
        setTimeout(function () {
            E._doSetting = false
        },
		10)
    }
});
HbyG(mini.DataBinding, "databinding");
mini.DataSet = function () {
    this._sources = {};
    this._data = {};
    this._links = [];
    this.U$w = {};
    mini.DataSet["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.DataSet, mini.Component, {
    add: function (_, $) {
        if (!_ || !$) return;
        this._sources[_] = $;
        this._data[_] = [];
        $.autoCreateNewID = true;
        $.IsQW = $.getIdField();
        $.Wks = false;
        $.on("addrow", this.J2K, this);
        $.on("updaterow", this.J2K, this);
        $.on("deleterow", this.J2K, this);
        $.on("removerow", this.J2K, this);
        $.on("preload", this.DbR, this);
        $.on("selectionchanged", this.NR, this)
    },
    addLink: function (B, _, $) {
        if (!B || !_ || !$) return;
        if (!this._sources[B] || !this._sources[_]) return;
        var A = {
            parentName: B,
            childName: _,
            parentField: $
        };
        this._links.push(A)
    },
    clearData: function () {
        this._data = {};
        this.U$w = {};
        for (var $ in this._sources) this._data = []
    },
    getData: function () {
        return this._data
    },
    _getNameByListControl: function ($) {
        for (var A in this._sources) {
            var _ = this._sources[A];
            if (_ == $) return A
        }
    },
    _getRecord: function (E, _, D) {
        var B = this._data[E];
        if (!B) return false;
        for (var $ = 0, C = B.length; $ < C; $++) {
            var A = B[$];
            if (A[D] == _[D]) return A
        }
        return null
    },
    J2K: function (F) {
        var C = F.type,
		_ = F.record,
		D = this._getNameByListControl(F.sender),
		E = this._getRecord(D, _, F.sender.getIdField()),
		A = this._data[D];
        if (E) {
            A = this._data[D];
            A.remove(E)
        }
        if (C == "removerow" && _._state == "added");
        else A.push(_);
        this.U$w[D] = F.sender.U$w;
        if (_._state == "added") {
            var $ = this._getParentSource(F.sender);
            if ($) {
                var B = $["getSelected"]();
                if (B) _._parentId = B[$.getIdField()];
                else A.remove(_)
            }
        }
    },
    DbR: function (M) {
        var J = M.sender,
		L = this._getNameByListControl(J),
		K = M.sender.getIdField(),
		A = this._data[L],
		$ = {};
        for (var F = 0, C = A.length; F < C; F++) {
            var G = A[F];
            $[G[K]] = G
        }
        var N = this.U$w[L];
        if (N) J.U$w = N;
        var I = M.data || [];
        for (F = 0, C = I.length; F < C; F++) {
            var G = I[F],
			H = $[G[K]];
            if (H) {
                delete H._uid;
                mini.copyTo(G, H)
            }
        }
        var D = this._getParentSource(J);
        if (J.getPageIndex && J.getPageIndex() == 0) {
            var E = [];
            for (F = 0, C = A.length; F < C; F++) {
                G = A[F];
                if (G._state == "added") if (D) {
                    var B = D["getSelected"]();
                    if (B && B[D.getIdField()] == G._parentId) E.push(G)
                } else E.push(G)
            }
            E.reverse();
            I.insertRange(0, E)
        }
        var _ = [];
        for (F = I.length - 1; F >= 0; F--) {
            G = I[F],
			H = $[G[K]];
            if (H && H._state == "removed") {
                I.removeAt(F);
                _.push(H)
            }
        }
    },
    _getParentSource: function (C) {
        var _ = this._getNameByListControl(C);
        for (var $ = 0, B = this._links.length; $ < B; $++) {
            var A = this._links[$];
            if (A.childName == _) return this._sources[A.parentName]
        }
    },
    _getLinks: function (B) {
        var C = this._getNameByListControl(B),
		D = [];
        for (var $ = 0, A = this._links.length; $ < A; $++) {
            var _ = this._links[$];
            if (_.parentName == C) D.push(_)
        }
        return D
    },
    NR: function (G) {
        var A = G.sender,
		_ = A["getSelected"](),
		F = this._getLinks(A);
        for (var $ = 0, E = F.length; $ < E; $++) {
            var D = F[$],
			C = this._sources[D.childName];
            if (_) {
                var B = {};
                B[D.parentField] = _[A.getIdField()];
                C.load(B)
            } else C["loadData"]([])
        }
    }
});
HbyG(mini.DataSet, "dataset");
mini.Hidden = function () {
    mini.Hidden["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Hidden, mini.Control, {
    _clearBorder: false,
    formField: true,
    value: "",
    uiCls: "mini-hidden",
    _create: function () {
        this.el = document.createElement("input");
        this.el.type = "hidden";
        this.el.className = "mini-hidden"
    },
    setName: function ($) {
        this.name = $;
        this.el.name = $
    },
    setValue: function ($) {
        if ($ === null || $ === undefined) $ = "";
        this.el.value = $
    },
    getValue: function () {
        return this.el.value
    },
    getFormValue: function () {
        return this.getValue()
    }
});
HbyG(mini.Hidden, "hidden");
mini.Popup = function () {
    mini.Popup["superclass"]["constructor"]["call"](this);
    this["setVisible"](false);
    this.setAllowDrag(this.allowDrag);
    this.setAllowResize(this["allowResize"])
};
Pv_r(mini.Popup, mini.Control, {
    _clearBorder: false,
    uiCls: "mini-popup",
    _create: function () {
        var $ = this.el = document.createElement("div");
        this.el.className = "mini-popup";
        this._contentEl = this.el
    },
    _initEvents: function () {
        KJ_T(function () {
            _pS(this.el, "mouseover", this.Vv5, this)
        },
		this)
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        mini.Popup["superclass"]["doLayout"]["call"](this);
        this.E2fJ();
        var A = this.el.childNodes;
        if (A) for (var $ = 0, B = A.length; $ < B; $++) {
            var _ = A[$];
            mini.layout(_)
        }
    },
    destroy: function ($) {
        if (this.el) this.el.onmouseover = null;
        mini.removeChilds(this._contentEl);
        BP(document, "mousedown", this.LzKR, this);
        BP(window, "resize", this.CZh, this);
        if (this.T4) {
            jQuery(this.T4).remove();
            this.T4 = null
        }
        if (this.shadowEl) {
            jQuery(this.shadowEl).remove();
            this.shadowEl = null
        }
        mini.Popup["superclass"]["destroy"]["call"](this, $)
    },
    setBody: function (_) {
        if (!_) return;
        if (!mini.isArray(_)) _ = [_];
        for (var $ = 0, A = _.length; $ < A; $++) mini.append(this._contentEl, _[$])
    },
    getAttrs: function ($) {
        var A = mini.Popup["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, A, ["popupEl", "popupCls", "showAction", "hideAction", "hAlign", "vAlign", "modalStyle", "onbeforeopen", "open", "onbeforeclose", "onclose"]);
        mini["_ParseBool"]($, A, ["showModal", "showShadow", "allowDrag", "allowResize"]);
        mini["_ParseInt"]($, A, ["showDelay", "hideDelay", "hOffset", "vOffset", "minWidth", "minHeight", "maxWidth", "maxHeight"]);
        var _ = mini["getChildNodes"]($, true);
        A.body = _;
        return A
    }
});
HbyG(mini.Popup, "popup");
SEV5_prototype = {
    isPopup: false,
    popupEl: null,
    popupCls: "",
    showAction: "mouseover",
    hideAction: "outerclick",
    showDelay: 300,
    hideDelay: 500,
    hAlign: "left",
    vAlign: "below",
    hOffset: 0,
    vOffset: 0,
    minWidth: 50,
    minHeight: 25,
    maxWidth: 2000,
    maxHeight: 2000,
    showModal: false,
    showShadow: true,
    modalStyle: "opacity:0.2",
    MSr: "mini-popup-drag",
    P34: "mini-popup-resize",
    allowDrag: false,
    allowResize: false,
    MB: function () {
        if (!this.popupEl) return;
        BP(this.popupEl, "click", this.Nis, this);
        BP(this.popupEl, "contextmenu", this.Fvd, this);
        BP(this.popupEl, "mouseover", this.Vv5, this)
    },
    Dh: function () {
        if (!this.popupEl) return;
        VNV(this.popupEl, "click", this.Nis, this);
        VNV(this.popupEl, "contextmenu", this.Fvd, this);
        VNV(this.popupEl, "mouseover", this.Vv5, this)
    },
    doShow: function (A) {
        var $ = {
            popupEl: this.popupEl,
            htmlEvent: A,
            cancel: false
        };
        this.fire("BeforeOpen", $);
        if ($.cancel == true) return;
        this.fire("opening", $);
        if ($.cancel == true) return;
        if (!this.popupEl) this.show();
        else {
            var _ = {};
            if (A) _.xy = [A.pageX, A.pageY];
            this.showAtEl(this.popupEl, _)
        }
    },
    doHide: function (_) {
        var $ = {
            popupEl: this.popupEl,
            htmlEvent: _,
            cancel: false
        };
        this.fire("BeforeClose", $);
        if ($.cancel == true) return;
        this.close()
    },
    show: function (_, $) {
        this.showAtPos(_, $)
    },
    showAtPos: function (B, A) {
        this["render"](document.body);
        if (!B) B = "center";
        if (!A) A = "middle";
        this.el.style.position = "absolute";
        this.el.style.left = "-2000px";
        this.el.style.top = "-2000px";
        this.el.style.display = "";
        this.UdCU();
        var _ = mini.getViewportBox(),
		$ = EcG(this.el);
        if (B == "left") B = 0;
        if (B == "center") B = _.width / 2 - $.width / 2;
        if (B == "right") B = _.width - $.width;
        if (A == "top") A = 0;
        if (A == "middle") A = _.y + _.height / 2 - $.height / 2;
        if (A == "bottom") A = _.height - $.height;
        if (B + $.width > _.right) B = _.right - $.width;
        if (A + $.height > _.bottom) A = _.bottom - $.height;
        this.BTXe(B, A)
    },
    T3d: function () {
        jQuery(this.T4).remove();
        if (!this["showModal"]) return;
        if (this.visible == false) return;
        var $ = document.documentElement,
		A = parseInt(Math.max(document.body.scrollWidth, $ ? $.scrollWidth : 0)),
		D = parseInt(Math.max(document.body.scrollHeight, $ ? $.scrollHeight : 0)),
		C = mini.getViewportBox(),
		B = C.height;
        if (B < D) B = D;
        var _ = C.width;
        if (_ < A) _ = A;
        this.T4 = mini.append(document.body, "<div class=\"mini-modal\"></div>");
        this.T4.style.height = B + "px";
        this.T4.style.width = _ + "px";
        this.T4.style.zIndex = YI(this.el, "zIndex") - 1;
        BIT(this.T4, this.modalStyle)
    },
    E2fJ: function () {
        if (!this.shadowEl) this.shadowEl = mini.append(document.body, "<div class=\"mini-shadow\"></div>");
        this.shadowEl.style.display = this["showShadow"] ? "" : "none";
        if (this["showShadow"]) {
            var $ = EcG(this.el),
			A = this.shadowEl.style;
            A.width = $.width + "px";
            A.height = $.height + "px";
            A.left = $.x + "px";
            A.top = $.y + "px";
            var _ = YI(this.el, "zIndex");
            if (!isNaN(_)) this.shadowEl.style.zIndex = _ - 2
        }
    },
    UdCU: function () {
        this.el.style.display = "";
        var $ = EcG(this.el);
        if ($.width > this.maxWidth) {
            SI9N(this.el, this.maxWidth);
            $ = EcG(this.el)
        }
        if ($.height > this.maxHeight) {
            Quj1(this.el, this.maxHeight);
            $ = EcG(this.el)
        }
        if ($.width < this.minWidth) {
            SI9N(this.el, this.minWidth);
            $ = EcG(this.el)
        }
        if ($.height < this.minHeight) {
            Quj1(this.el, this.minHeight);
            $ = EcG(this.el)
        }
    },
    showAtEl: function (H, D) {
        H = E1R(H);
        if (!H) return;
        if (!this.isRender() || this.el.parentNode != document.body) this["render"](document.body);
        var A = {
            hAlign: this.hAlign,
            vAlign: this.vAlign,
            hOffset: this.hOffset,
            vOffset: this.vOffset,
            popupCls: this.popupCls
        };
        mini.copyTo(A, D);
        RE(H, A.popupCls);
        H.popupCls = A.popupCls;
        this._popupEl = H;
        this.el.style.position = "absolute";
        this.el.style.left = "-2000px";
        this.el.style.top = "-2000px";
        this.el.style.display = "";
        this["doLayout"]();
        this.UdCU();
        var J = mini.getViewportBox(),
		B = EcG(this.el),
		L = EcG(H),
		F = A.xy,
		C = A.hAlign,
		E = A.vAlign,
		M = J.width / 2 - B.width / 2,
		K = 0;
        if (F) {
            M = F[0];
            K = F[1]
        }
        switch (A.hAlign) {
            case "outleft":
                M = L.x - B.width;
                break;
            case "left":
                M = L.x;
                break;
            case "center":
                M = L.x + L.width / 2 - B.width / 2;
                break;
            case "right":
                M = L.right - B.width;
                break;
            case "outright":
                M = L.right;
                break;
            default:
                break
        }
        switch (A.vAlign) {
            case "above":
                K = L.y - B.height;
                break;
            case "top":
                K = L.y;
                break;
            case "middle":
                K = L.y + L.height / 2 - B.height / 2;
                break;
            case "bottom":
                K = L.bottom - B.height;
                break;
            case "below":
                K = L.bottom;
                break;
            default:
                break
        }
        M = parseInt(M);
        K = parseInt(K);
        if (A.outVAlign || A.outHAlign) {
            if (A.outVAlign == "above") if (K + B.height > J.bottom) {
                var _ = L.y - J.y,
				I = J.bottom - L.bottom;
                if (_ > I) K = L.y - B.height
            }
            if (A.outHAlign == "outleft") if (M + B.width > J.right) {
                var G = L.x - J.x,
				$ = J.right - L.right;
                if (G > $) M = L.x - B.width
            }
            if (A.outHAlign == "right") if (M + B.width > J.right) M = L.right - B.width;
            this.BTXe(M, K)
        } else this.showAtPos(M + A.hOffset, K + A.vOffset)
    },
    BTXe: function (A, _) {
        this.el.style.display = "";
        this.el.style.zIndex = mini.getMaxZIndex();
        mini.setX(this.el, A);
        mini.setY(this.el, _);
        this["setVisible"](true);
        if (this.hideAction == "mouseout") VNV(document, "mousemove", this.TjLh, this);
        var $ = this;
        this.E2fJ();
        this.T3d();
        mini.layoutIFrames(this.el);
        this.isPopup = true;
        VNV(document, "mousedown", this.LzKR, this);
        VNV(window, "resize", this.CZh, this);
        this.fire("Open")
    },
    open: function () {
        this.show()
    },
    close: function () {
        this.hide()
    },
    hide: function () {
        if (!this.el) return;
        if (this.popupEl) WeL(this.popupEl, this.popupEl.popupCls);
        if (this._popupEl) WeL(this._popupEl, this._popupEl.popupCls);
        this._popupEl = null;
        jQuery(this.T4).remove();
        if (this.shadowEl) this.shadowEl.style.display = "none";
        BP(document, "mousemove", this.TjLh, this);
        BP(document, "mousedown", this.LzKR, this);
        BP(window, "resize", this.CZh, this);
        this["setVisible"](false);
        this.isPopup = false;
        this.fire("Close")
    },
    setPopupEl: function ($) {
        $ = E1R($);
        if (!$) return;
        this.MB();
        this.popupEl = $;
        this.Dh()
    },
    setPopupCls: function ($) {
        this.popupCls = $
    },
    setShowAction: function ($) {
        this.showAction = $
    },
    setHideAction: function ($) {
        this.hideAction = $
    },
    setShowDelay: function ($) {
        this.showDelay = $
    },
    setHideDelay: function ($) {
        this.hideDelay = $
    },
    setHAlign: function ($) {
        this.hAlign = $
    },
    setVAlign: function ($) {
        this.vAlign = $
    },
    setHOffset: function ($) {
        $ = parseInt($);
        if (isNaN($)) $ = 0;
        this.hOffset = $
    },
    setVOffset: function ($) {
        $ = parseInt($);
        if (isNaN($)) $ = 0;
        this.vOffset = $
    },
    setShowModal: function ($) {
        this["showModal"] = $
    },
    setShowShadow: function ($) {
        this["showShadow"] = $
    },
    setMinWidth: function ($) {
        if (isNaN($)) return;
        this.minWidth = $
    },
    setMinHeight: function ($) {
        if (isNaN($)) return;
        this.minHeight = $
    },
    setMaxWidth: function ($) {
        if (isNaN($)) return;
        this.maxWidth = $
    },
    setMaxHeight: function ($) {
        if (isNaN($)) return;
        this.maxHeight = $
    },
    setAllowDrag: function ($) {
        this.allowDrag = $;
        WeL(this.el, this.MSr);
        if ($) RE(this.el, this.MSr)
    },
    setAllowResize: function ($) {
        this["allowResize"] = $;
        WeL(this.el, this.P34);
        if ($) RE(this.el, this.P34)
    },
    Nis: function (_) {
        if (this._g) return;
        if (this.showAction != "leftclick") return;
        var $ = jQuery(this.popupEl).attr("allowPopup");
        if (String($) == "false") return;
        this.doShow(_)
    },
    Fvd: function (_) {
        if (this._g) return;
        if (this.showAction != "rightclick") return;
        var $ = jQuery(this.popupEl).attr("allowPopup");
        if (String($) == "false") return;
        _.preventDefault();
        this.doShow(_)
    },
    Vv5: function (A) {
        if (this._g) return;
        if (this.showAction != "mouseover") return;
        var _ = jQuery(this.popupEl).attr("allowPopup");
        if (String(_) == "false") return;
        clearTimeout(this._hideTimer);
        this._hideTimer = null;
        if (this.isPopup) return;
        var $ = this;
        this._showTimer = setTimeout(function () {
            $.doShow(A)
        },
		this.showDelay)
    },
    TjLh: function ($) {
        if (this.hideAction != "mouseout") return;
        this.Zq($)
    },
    LzKR: function ($) {
        if (this.hideAction != "outerclick") return;
        if (!this.isPopup) return;
        if (this["within"]($) || (this.popupEl && Yma(this.popupEl, $.target)));
        else this.doHide($)
    },
    Zq: function (_) {
        if (Yma(this.el, _.target) || (this.popupEl && Yma(this.popupEl, _.target)));
        else {
            clearTimeout(this._showTimer);
            this._showTimer = null;
            if (this._hideTimer) return;
            var $ = this;
            this._hideTimer = setTimeout(function () {
                $.doHide(_)
            },
			this.hideDelay)
        }
    },
    CZh: function ($) {
        if (this["isDisplay"]() && !mini.isIE6) this.T3d()
    }
};
mini.copyTo(mini.Popup.prototype, SEV5_prototype);
mini.Button = function () {
    mini.Button["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Button, mini.Control, {
    text: "",
    iconCls: "",
    iconStyle: "",
    plain: false,
    checkOnClick: false,
    checked: false,
    groupName: "",
    LiA: "mini-button-plain",
    _hoverCls: "mini-button-hover",
    ZyR: "mini-button-pressed",
    R2Z: "mini-button-checked",
    ZSXk: "mini-button-disabled",
    allowCls: "",
    _clearBorder: false,
    set: function ($) {
        if (typeof $ == "string") return this;
        this.N2b = $.text || $["iconStyle"] || $.iconCls || $.iconPosition;
        mini.Button["superclass"].set["call"](this, $);
        if (this.N2b === false) {
            this.N2b = true;
            this["doUpdate"]()
        }
        return this
    },
    uiCls: "mini-button",
    _create: function () {
        this.el = document.createElement("a");
        this.el.className = "mini-button";
        this.el.hideFocus = true;
        this.el.href = "javascript:void(0)";
        this["doUpdate"]()
    },
    _initEvents: function () {
        KJ_T(function () {
            _pS(this.el, "mousedown", this.UNAE, this);
            _pS(this.el, "click", this.YY, this)
        },
		this)
    },
    destroy: function ($) {
        if (this.el) {
            this.el.onclick = null;
            this.el.onmousedown = null
        }
        if (this.menu) this.menu.owner = null;
        this.menu = null;
        mini.Button["superclass"]["destroy"]["call"](this, $)
    },
    doUpdate: function () {
        if (this.N2b === false) return;
        var _ = "",
		$ = this.text;
        if (this.iconCls && $) _ = " mini-button-icon " + this.iconCls;
        else if (this.iconCls && $ === "") {
            _ = " mini-button-iconOnly " + this.iconCls;
            $ = "&nbsp;"
        }
        var A = "<span class=\"mini-button-text " + _ + "\">" + $ + "</span>";
        if (this.allowCls) A = A + "<span class=\"mini-button-allow " + this.allowCls + "\"></span>";
        this.el.innerHTML = A
    },
    href: "",
    setHref: function ($) {
        this.href = $;
        this.el.href = $;
        var _ = this.el;
        setTimeout(function () {
            _.onclick = null
        },
		100)
    },
    getHref: function () {
        return this.href
    },
    target: "",
    setTarget: function ($) {
        this.target = $;
        this.el.target = $
    },
    getTarget: function () {
        return this.target
    },
    setText: function ($) {
        if (this.text != $) {
            this.text = $;
            this["doUpdate"]()
        }
    },
    getText: function () {
        return this.text
    },
    setIconCls: function ($) {
        this.iconCls = $;
        this["doUpdate"]()
    },
    getIconCls: function () {
        return this.iconCls
    },
    setIconStyle: function ($) {
        this["iconStyle"] = $;
        this["doUpdate"]()
    },
    getIconStyle: function () {
        return this["iconStyle"]
    },
    setIconPosition: function ($) {
        this.iconPosition = "left";
        this["doUpdate"]()
    },
    getIconPosition: function () {
        return this.iconPosition
    },
    setPlain: function ($) {
        this.plain = $;
        if ($) this["addCls"](this.LiA);
        else this["removeCls"](this.LiA)
    },
    getPlain: function () {
        return this.plain
    },
    setGroupName: function ($) {
        this["groupName"] = $
    },
    getGroupName: function () {
        return this["groupName"]
    },
    setCheckOnClick: function ($) {
        this["checkOnClick"] = $
    },
    getCheckOnClick: function () {
        return this["checkOnClick"]
    },
    setChecked: function ($) {
        var _ = this.checked != $;
        this.checked = $;
        if ($) this["addCls"](this.R2Z);
        else this["removeCls"](this.R2Z);
        if (_) this.fire("CheckedChanged")
    },
    getChecked: function () {
        return this.checked
    },
    doClick: function () {
        this.YY(null)
    },
    YY: function (D) {
        if (this["isReadOnly"]()) return;
        this.focus();
        if (this["checkOnClick"]) if (this["groupName"]) {
            var _ = this["groupName"],
			C = mini.findControls(function ($) {
			    if ($.type == "button" && $["groupName"] == _) return true
			});
            if (C.length > 0) {
                for (var $ = 0, A = C.length; $ < A; $++) {
                    var B = C[$];
                    if (B != this) B.setChecked(false)
                }
                this.setChecked(true)
            } else this.setChecked(!this.checked)
        } else this.setChecked(!this.checked);
        this.fire("click", {
            htmlEvent: D
        });
        return false
    },
    UNAE: function ($) {
        if (this["isReadOnly"]()) return;
        this["addCls"](this.ZyR);
        VNV(document, "mouseup", this.NkrJ, this)
    },
    NkrJ: function ($) {
        this["removeCls"](this.ZyR);
        BP(document, "mouseup", this.NkrJ, this)
    },
    onClick: function (_, $) {
        this.on("click", _, $)
    },
    getAttrs: function ($) {
        var _ = mini.Button["superclass"]["getAttrs"]["call"](this, $);
        _.text = $.innerHTML;
        mini["_ParseString"]($, _, ["text", "href", "iconCls", "iconStyle", "iconPosition", "groupName", "menu", "onclick", "oncheckedchanged", "target"]);
        mini["_ParseBool"]($, _, ["plain", "checkOnClick", "checked"]);
        return _
    }
});
HbyG(mini.Button, "button");
OECButton = function () {
    OECButton["superclass"]["constructor"]["call"](this)
};
Pv_r(OECButton, mini.Button, {
    uiCls: "mini-menubutton",
    allowCls: "mini-button-menu",
    setMenu: function ($) {
        if (mini.isArray($)) $ = {
            type: "menu",
            items: $
        };
        if (typeof $ == "string") {
            var _ = E1R($);
            if (!_) return;
            mini.parse($);
            $ = mini.get($)
        }
        if (this.menu !== $) {
            this.menu = mini.getAndCreate($);
            this.menu.setPopupEl(this.el);
            this.menu.setPopupCls("mini-button-popup");
            this.menu.setShowAction("leftclick");
            this.menu.setHideAction("outerclick");
            this.menu.setHAlign("left");
            this.menu.setVAlign("below");
            this.menu.hide();
            this.menu.owner = this
        }
    },
    setEnabled: function ($) {
        this.enabled = $;
        if ($) this["removeCls"](this.ZSXk);
        else this["addCls"](this.ZSXk);
        jQuery(this.el).attr("allowPopup", !!$)
    }
});
HbyG(OECButton, "menubutton");
mini.SplitButton = function () {
    mini.SplitButton["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.SplitButton, OECButton, {
    uiCls: "mini-splitbutton",
    allowCls: "mini-button-split"
});
HbyG(mini.SplitButton, "splitbutton");
mini.CheckBox = function () {
    mini.CheckBox["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.CheckBox, mini.Control, {
    formField: true,
    text: "",
    checked: false,
    defaultValue: false,
    trueValue: true,
    falseValue: false,
    uiCls: "mini-checkbox",
    _create: function () {
        var $ = this.uid + "$check";
        this.el = document.createElement("span");
        this.el.className = "mini-checkbox";
        this.el.innerHTML = "<input id=\"" + $ + "\" name=\"" + this.id + "\" type=\"checkbox\" class=\"mini-checkbox-check\"><label for=\"" + $ + "\" onclick=\"return false;\">" + this.text + "</label>";
        this.G23X = this.el.firstChild;
        this.A9fe = this.el.lastChild
    },
    destroy: function ($) {
        if (this.G23X) {
            this.G23X.onmouseup = null;
            this.G23X.onclick = null;
            this.G23X = null
        }
        mini.CheckBox["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "click", this.Sqft, this);
            this.G23X.onmouseup = function () {
                return false
            };
            var $ = this;
            this.G23X.onclick = function () {
                if ($["isReadOnly"]()) return false
            }
        },
		this)
    },
    setName: function ($) {
        this.name = $;
        mini.setAttr(this.G23X, "name", this.name)
    },
    setText: function ($) {
        if (this.text !== $) {
            this.text = $;
            this.A9fe.innerHTML = $
        }
    },
    getText: function () {
        return this.text
    },
    setChecked: function ($) {
        if ($ === true) $ = true;
        else if ($ == this.trueValue) $ = true;
        else if ($ == "true") $ = true;
        else if ($ === 1) $ = true;
        else if ($ == "Y") $ = true;
        else $ = false;
        if (this.checked !== $) {
            this.checked = !!$;
            this.G23X.checked = this.checked;
            this.value = this.getValue()
        }
    },
    getChecked: function () {
        return this.checked
    },
    setValue: function ($) {
        if (this.checked != $) {
            this.setChecked($);
            this.value = this.getValue()
        }
    },
    getValue: function () {
        return String(this.checked == true ? this.trueValue : this.falseValue)
    },
    getFormValue: function () {
        return this.getValue()
    },
    setTrueValue: function ($) {
        this.G23X.value = $;
        this.trueValue = $
    },
    getTrueValue: function () {
        return this.trueValue
    },
    setFalseValue: function ($) {
        this.falseValue = $
    },
    getFalseValue: function () {
        return this.falseValue
    },
    Sqft: function ($) {
        if (this["isReadOnly"]()) return;
        this.setChecked(!this.checked);
        this.fire("checkedchanged", {
            checked: this.checked
        });
        this.fire("valuechanged", {
            value: this.getValue()
        });
        this.fire("click", $, this)
    },
    getAttrs: function (A) {
        var D = mini.CheckBox["superclass"]["getAttrs"]["call"](this, A),
		C = jQuery(A);
        D.text = A.innerHTML;
        mini["_ParseString"](A, D, ["text", "oncheckedchanged", "onclick", "onvaluechanged"]);
        mini["_ParseBool"](A, D, ["enabled"]);
        var B = mini.getAttr(A, "checked");
        if (B) D.checked = (B == "true" || B == "checked") ? true : false;
        var _ = C.attr("trueValue");
        if (_) {
            D.trueValue = _;
            _ = parseInt(_);
            if (!isNaN(_)) D.trueValue = _
        }
        var $ = C.attr("falseValue");
        if ($) {
            D.falseValue = $;
            $ = parseInt($);
            if (!isNaN($)) D.falseValue = $
        }
        return D
    }
});
HbyG(mini.CheckBox, "checkbox");
mini.ButtonEdit = function () {
    mini.ButtonEdit["superclass"]["constructor"]["call"](this);
    var $ = this["isReadOnly"]();
    if ($ || this.allowInput == false) this.VXt["readOnly"] = true;
    if (this.enabled == false) this["addCls"](this.ZSXk);
    if ($) this["addCls"](this.AOf);
    if (this.required) this["addCls"](this.F59)
};
Pv_r(mini.ButtonEdit, mini.ValidatorBase, {
    name: "",
    formField: true,
    defaultValue: "",
    value: "",
    text: "",
    emptyText: "",
    maxLength: 1000,
    minLength: 0,
    width: 125,
    height: 21,
    inputAsValue: false,
    allowInput: true,
    Wsv: "mini-buttonedit-noInput",
    AOf: "mini-buttonedit-readOnly",
    ZSXk: "mini-buttonedit-disabled",
    UVcc: "mini-buttonedit-empty",
    FS: "mini-buttonedit-focus",
    U_y: "mini-buttonedit-button",
    Ifmt: "mini-buttonedit-button-hover",
    UI6: "mini-buttonedit-button-pressed",
    set: function ($) {
        if (typeof $ == "string") return this;
        this.N2b = !($.enabled == false || $.allowInput == false || $["readOnly"]);
        mini.ButtonEdit["superclass"].set["call"](this, $);
        if (this.N2b === false) {
            this.N2b = true;
            this["doUpdate"]()
        }
        return this
    },
    uiCls: "mini-buttonedit",
    LjKHtml: function () {
        var $ = "onmouseover=\"RE(this,'" + this.Ifmt + "');\" " + "onmouseout=\"WeL(this,'" + this.Ifmt + "');\"";
        return "<span class=\"mini-buttonedit-button\" " + $ + "><span class=\"mini-buttonedit-icon\"></span></span>"
    },
    _create: function () {
        this.el = document.createElement("span");
        this.el.className = "mini-buttonedit";
        var $ = this.LjKHtml();
        this.el.innerHTML = "<span class=\"mini-buttonedit-border\"><input type=\"input\" class=\"mini-buttonedit-input\" autocomplete=\"off\"/>" + $ + "</span><input name=\"" + this.name + "\" type=\"hidden\"/>";
        this._firstChild = this.el.firstChild;
        this.VXt = this._firstChild.firstChild;
        this.Izde = this.el.lastChild;
        this._buttonEl = this._firstChild.lastChild
    },
    destroy: function ($) {
        if (this.el) {
            this.el.onmousedown = null;
            this.el.onmousewheel = null;
            this.el.onmouseover = null;
            this.el.onmouseout = null
        }
        if (this.VXt) {
            this.VXt.onchange = null;
            this.VXt.onfocus = null;
            mini["clearEvent"](this.VXt);
            this.VXt = null
        }
        mini.ButtonEdit["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        KJ_T(function () {
            _pS(this.el, "mousedown", this.UNAE, this);
            _pS(this.VXt, "focus", this.Bu6, this);
            _pS(this.VXt, "change", this.DvF, this)
        },
		this)
    },
    C_s: false,
    U5I: function () {
        if (this.C_s) return;
        this.C_s = true;
        VNV(this.el, "click", this.YY, this);
        VNV(this.VXt, "blur", this._Tg, this);
        VNV(this.VXt, "keydown", this.$Qvl, this);
        VNV(this.VXt, "keyup", this.Zjo, this);
        VNV(this.VXt, "keypress", this.L2Z, this)
    },
    _buttonWidth: 20,
    doLayout: function () {
        if (!this.canLayout()) return;
        mini.ButtonEdit["superclass"]["doLayout"]["call"](this);
        var $ = L_h(this.el);
        if (this.el.style.width == "100%") $ -= 1;
        if (this.PMX) $ -= 18;
        $ -= 2;
        this._firstChild.style.width = $ + "px";
        $ -= this._buttonWidth;
        if (this.el.style.width == "100%") $ -= 1;
        if ($ < 0) $ = 0;
        this.VXt.style.width = $ + "px"
    },
    setHeight: function ($) {
        if (parseInt($) == $) $ += "px";
        this.height = $
    },
    N_MV: function () { },
    focus: function () {
        try {
            this.VXt.focus();
            var $ = this;
            setTimeout(function () {
                if ($.FNnW) $.VXt.focus()
            },
			10)
        } catch (_) { }
    },
    blur: function () {
        try {
            this.VXt.blur()
        } catch ($) { }
    },
    selectText: function () {
        this.VXt["select"]()
    },
    getTextEl: function () {
        return this.VXt
    },
    setName: function ($) {
        this.name = $;
        this.Izde.name = $
    },
    setEmptyText: function ($) {
        if ($ === null || $ === undefined) $ = "";
        this["emptyText"] = $;
        this.N_MV()
    },
    getEmptyText: function () {
        return this["emptyText"]
    },
    setText: function ($) {
        if ($ === null || $ === undefined) $ = "";
        var _ = this.text !== $;
        this.text = $;
        this.VXt.value = $
    },
    getText: function () {
        var $ = this.VXt.value;
        return $ != this["emptyText"] ? $ : ""
    },
    setValue: function ($) {
        if ($ === null || $ === undefined) $ = "";
        var _ = this.value !== $;
        this.value = $;
        this.N_MV()
    },
    getValue: function () {
        return this.value
    },
    getFormValue: function () {
        value = this.value;
        if (value === null || value === undefined) value = "";
        return String(value)
    },
    setMaxLength: function ($) {
        $ = parseInt($);
        if (isNaN($)) return;
        this.maxLength = $;
        this.VXt.maxLength = $
    },
    getMaxLength: function () {
        return this.maxLength
    },
    setMinLength: function ($) {
        $ = parseInt($);
        if (isNaN($)) return;
        this.minLength = $
    },
    getMinLength: function () {
        return this.minLength
    },
    _doReadOnly: function () {
        var $ = this["isReadOnly"]();
        if ($ || this.allowInput == false) this.VXt["readOnly"] = true;
        else this.VXt["readOnly"] = false;
        if ($) this["addCls"](this.AOf);
        else this["removeCls"](this.AOf);
        if (this.allowInput) this["removeCls"](this.Wsv);
        else this["addCls"](this.Wsv)
    },
    setAllowInput: function ($) {
        this.allowInput = $;
        this._doReadOnly()
    },
    getAllowInput: function () {
        return this.allowInput
    },
    setInputAsValue: function ($) {
        this.inputAsValue = $
    },
    getInputAsValue: function () {
        return this.inputAsValue
    },
    PMX: null,
    getErrorIconEl: function () {
        if (!this.PMX) this.PMX = mini.append(this.el, "<span class=\"mini-errorIcon\"></span>");
        return this.PMX
    },
    Keo: function () {
        if (this.PMX) {
            var $ = this.PMX;
            jQuery($).remove()
        }
        this.PMX = null
    },
    YY: function ($) {
        if (this["isReadOnly"]() || this.enabled == false) return;
        if (Yma(this._buttonEl, $.target)) this.QN($)
    },
    UNAE: function (B) {
        if (this["isReadOnly"]() || this.enabled == false) return;
        if (!Yma(this.VXt, B.target)) {
            var $ = this;
            setTimeout(function () {
                $.focus();
                mini.selectRange($.VXt, 1000, 1000)
            },
			1);
            if (Yma(this._buttonEl, B.target)) {
                var _ = MRj9(B.target, "mini-buttonedit-up"),
				A = MRj9(B.target, "mini-buttonedit-down");
                if (_) {
                    RE(_, this.UI6);
                    this.G$q(B, "up")
                } else if (A) {
                    RE(A, this.UI6);
                    this.G$q(B, "down")
                } else {
                    RE(this._buttonEl, this.UI6);
                    this.G$q(B)
                }
                VNV(document, "mouseup", this.NkrJ, this)
            }
        }
    },
    NkrJ: function (_) {
        var $ = this;
        setTimeout(function () {
            var A = $._buttonEl.getElementsByTagName("*");
            for (var _ = 0, B = A.length; _ < B; _++) WeL(A[_], $.UI6);
            WeL($._buttonEl, $.UI6);
            WeL($.el, $.ZyR)
        },
		80);
        BP(document, "mouseup", this.NkrJ, this)
    },
    Bu6: function ($) {
        this["doUpdate"]();
        this.U5I();
        if (this["isReadOnly"]()) return;
        this.FNnW = true;
        this["addCls"](this.FS)
    },
    _Tg: function (_) {
        this.FNnW = false;
        var $ = this;
        setTimeout(function () {
            if ($.FNnW == false) $["removeCls"]($.FS)
        },
		2)
    },
    $Qvl: function (_) {
        this.fire("keydown", {
            htmlEvent: _
        });
        if (_.keyCode == 8 && (this["isReadOnly"]() || this.allowInput == false)) return false;
        if (_.keyCode == 13) {
            var $ = this;
            $.DvF(null);
            $.fire("enter")
        }
    },
    DvF: function () {
        var _ = this.VXt.value,
		$ = this.getValue();
        this["setValue"](_);
        if ($ !== this.getFormValue()) this.QWeD()
    },
    Zjo: function ($) {
        this.fire("keyup", {
            htmlEvent: $
        })
    },
    L2Z: function ($) {
        this.fire("keypress", {
            htmlEvent: $
        })
    },
    QN: function ($) {
        var _ = {
            htmlEvent: $,
            cancel: false
        };
        this.fire("beforebuttonclick", _);
        if (_.cancel == true) return;
        this.fire("buttonclick", _)
    },
    G$q: function (_, $) {
        this.focus();
        this["addCls"](this.FS);
        this.fire("buttonmousedown", {
            htmlEvent: _,
            spinType: $
        })
    },
    onButtonClick: function (_, $) {
        this.on("buttonclick", _, $)
    },
    onButtonMouseDown: function (_, $) {
        this.on("buttonmousedown", _, $)
    },
    onTextChanged: function (_, $) {
        this.on("textchanged", _, $)
    },
    textName: "",
    setTextName: function ($) {
        this.textName = $;
        if (this.VXt) mini.setAttr(this.VXt, "name", this.textName)
    },
    getTextName: function () {
        return this.textName
    },
    getAttrs: function ($) {
        var A = mini.ButtonEdit["superclass"]["getAttrs"]["call"](this, $),
		_ = jQuery($);
        mini["_ParseString"]($, A, ["value", "text", "textName", "onenter", "onkeydown", "onkeyup", "onkeypress", "onbuttonclick", "onbuttonmousedown", "ontextchanged"]);
        mini["_ParseBool"]($, A, ["allowInput", "inputAsValue"]);
        mini["_ParseInt"]($, A, ["maxLength", "minLength"]);
        return A
    }
});
HbyG(mini.ButtonEdit, "buttonedit");
mini.TextBox = function () {
    mini.TextBox["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.TextBox, mini.ValidatorBase, {
    name: "",
    formField: true,
    minHeight: 15,
    maxLength: 5000,
    emptyText: "",
    text: "",
    value: "",
    defaultValue: "",
    width: 125,
    height: 21,
    UVcc: "mini-textbox-empty",
    FS: "mini-textbox-focus",
    ZSXk: "mini-textbox-disabled",
    uiCls: "mini-textbox",
    $wSe: "text",
    _create: function () {
        var $ = "<input type=\"" + this.$wSe + "\" class=\"mini-textbox-input\" autocomplete=\"off\"/>";
        if (this.$wSe == "textarea") $ = "<textarea class=\"mini-textbox-input\" autocomplete=\"off\"/></textarea>";
        $ += "<input type=\"hidden\"/>";
        this.el = document.createElement("span");
        this.el.className = "mini-textbox";
        this.el.innerHTML = $;
        this.VXt = this.el.firstChild;
        this.Izde = this.el.lastChild;
        this._firstChild = this.VXt
    },
    _initEvents: function () {
        KJ_T(function () {
            _pS(this.VXt, "drop", this.__OnDropText, this);
            _pS(this.VXt, "change", this.DvF, this);
            _pS(this.VXt, "focus", this.Bu6, this);
            _pS(this.el, "mousedown", this.UNAE, this)
        },
		this);
        this.on("validation", this.FW6, this)
    },
    C_s: false,
    U5I: function () {
        if (this.C_s) return;
        this.C_s = true;
        VNV(this.VXt, "blur", this._Tg, this);
        VNV(this.VXt, "keydown", this.$Qvl, this);
        VNV(this.VXt, "keyup", this.Zjo, this);
        VNV(this.VXt, "keypress", this.L2Z, this)
    },
    destroy: function ($) {
        if (this.el) this.el.onmousedown = null;
        if (this.VXt) {
            this.VXt.ondrop = null;
            this.VXt.onchange = null;
            this.VXt.onfocus = null;
            mini["clearEvent"](this.VXt);
            this.VXt = null
        }
        if (this.Izde) {
            mini["clearEvent"](this.Izde);
            this.Izde = null
        }
        mini.TextBox["superclass"]["destroy"]["call"](this, $)
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        var $ = L_h(this.el);
        if (this.PMX) $ -= 18;
        $ -= 4;
        if (this.el.style.width == "100%") $ -= 1;
        if ($ < 0) $ = 0;
        this.VXt.style.width = $ + "px"
    },
    setHeight: function ($) {
        if (parseInt($) == $) $ += "px";
        this.height = $;
        if (this.$wSe == "textarea") {
            this.el.style.height = $;
            this["doLayout"]()
        }
    },
    setName: function ($) {
        if (this.name != $) {
            this.name = $;
            this.Izde.name = $
        }
    },
    setValue: function ($) {
        if ($ === null || $ === undefined) $ = "";
        $ = String($);
        if (this.value !== $) {
            this.value = $;
            this.Izde.value = this.VXt.value = $;
            this.N_MV()
        }
    },
    getValue: function () {
        return this.value
    },
    getFormValue: function () {
        value = this.value;
        if (value === null || value === undefined) value = "";
        return String(value)
    },
    setAllowInput: function ($) {
        if (this.allowInput != $) {
            this.allowInput = $;
            this["doUpdate"]()
        }
    },
    getAllowInput: function () {
        return this.allowInput
    },
    N_MV: function () {
        if (this.FNnW) return;
        if (this.value == "" && this["emptyText"]) {
            this.VXt.value = this["emptyText"];
            RE(this.el, this.UVcc)
        } else WeL(this.el, this.UVcc)
    },
    setEmptyText: function ($) {
        if (this["emptyText"] != $) {
            this["emptyText"] = $;
            this.N_MV()
        }
    },
    getEmptyText: function () {
        return this["emptyText"]
    },
    setMaxLength: function ($) {
        this.maxLength = $;
        mini.setAttr(this.VXt, "maxLength", $);
        if (this.$wSe == "textarea") VNV(this.VXt, "keypress", this.__OnMaxLengthKeyUp, this)
    },
    __OnMaxLengthKeyUp: function ($) {
        if (this.VXt.value.length >= this.maxLength) $.preventDefault()
    },
    getMaxLength: function () {
        return this.maxLength
    },
    setReadOnly: function ($) {
        if (this["readOnly"] != $) {
            this["readOnly"] = $;
            this["doUpdate"]()
        }
    },
    setEnabled: function ($) {
        if (this.enabled != $) {
            this.enabled = $;
            this["doUpdate"]()
        }
    },
    doUpdate: function () {
        if (this.enabled) this["removeCls"](this.ZSXk);
        else this["addCls"](this.ZSXk);
        if (this["isReadOnly"]() || this.allowInput == false) this.VXt["readOnly"] = true;
        else this.VXt["readOnly"] = false;
        if (this.required) this["addCls"](this.F59);
        else this["removeCls"](this.F59)
    },
    focus: function () {
        try {
            this.VXt.focus()
        } catch ($) { }
    },
    blur: function () {
        try {
            this.VXt.blur()
        } catch ($) { }
    },
    selectText: function () {
        this.VXt["select"]()
    },
    getTextEl: function () {
        return this.VXt
    },
    PMX: null,
    getErrorIconEl: function () {
        if (!this.PMX) this.PMX = mini.append(this.el, "<span class=\"mini-errorIcon\"></span>");
        return this.PMX
    },
    Keo: function () {
        if (this.PMX) {
            var $ = this.PMX;
            jQuery($).remove()
        }
        this.PMX = null
    },
    UNAE: function (_) {
        var $ = this;
        if (!Yma(this.VXt, _.target)) setTimeout(function () {
            $.focus();
            mini.selectRange($.VXt, 1000, 1000)
        },
		1);
        else setTimeout(function () {
            $.VXt.focus()
        },
		1)
    },
    DvF: function (A, _) {
        var $ = this.value;
        this["setValue"](this.VXt.value);
        if ($ !== this.getValue() || _ === true) this.QWeD()
    },
    __OnDropText: function (_) {
        var $ = this;
        setTimeout(function () {
            $.DvF(_)
        },
		0)
    },
    $Qvl: function (_) {
        this.fire("keydown", {
            htmlEvent: _
        });
        if (_.keyCode == 8 && (this["isReadOnly"]() || this.allowInput == false)) return false;
        if (_.keyCode == 13) {
            this.DvF(null, true);
            var $ = this;
            setTimeout(function () {
                $.fire("enter")
            },
			10)
        }
    },
    Zjo: function ($) {
        this.fire("keyup", {
            htmlEvent: $
        })
    },
    L2Z: function ($) {
        this.fire("keypress", {
            htmlEvent: $
        })
    },
    Bu6: function ($) {
        this["doUpdate"]();
        if (this["isReadOnly"]()) return;
        this.FNnW = true;
        this["addCls"](this.FS);
        this.U5I();
        WeL(this.el, this.UVcc);
        if (this["emptyText"] && this.VXt.value == this["emptyText"]) {
            this.VXt.value = "";
            this.VXt["select"]()
        }
    },
    _Tg: function (_) {
        this.FNnW = false;
        var $ = this;
        setTimeout(function () {
            if ($.FNnW == false) $["removeCls"]($.FS)
        },
		2);
        if (this["emptyText"] && this.VXt.value == "") {
            this.VXt.value = this["emptyText"];
            RE(this.el, this.UVcc)
        }
    },
    getAttrs: function ($) {
        var A = mini.TextBox["superclass"]["getAttrs"]["call"](this, $),
		_ = jQuery($);
        mini["_ParseString"]($, A, ["value", "text", "emptyText", "onenter", "onkeydown", "onkeyup", "onkeypress", "maxLengthErrorText", "minLengthErrorText", "vtype", "emailErrorText", "urlErrorText", "floatErrorText", "intErrorText", "dateErrorText", "minErrorText", "maxErrorText", "rangeLengthErrorText", "rangeErrorText", "rangeCharErrorText"]);
        mini["_ParseBool"]($, A, ["allowInput"]);
        mini["_ParseInt"]($, A, ["maxLength", "minLength", "minHeight"]);
        return A
    },
    vtype: "",
    setVtype: function ($) {
        this.vtype = $
    },
    getVtype: function () {
        return this.vtype
    },
    FW6: function ($) {
        if ($.isValid == false) return;
        mini._ValidateVType(this.vtype, $.value, $, this)
    },
    setEmailErrorText: function ($) {
        this.emailErrorText = $
    },
    getEmailErrorText: function () {
        return this.emailErrorText
    },
    setUrlErrorText: function ($) {
        this.urlErrorText = $
    },
    getUrlErrorText: function () {
        return this.urlErrorText
    },
    setFloatErrorText: function ($) {
        this.floatErrorText = $
    },
    getFloatErrorText: function () {
        return this.floatErrorText
    },
    setIntErrorText: function ($) {
        this.intErrorText = $
    },
    getIntErrorText: function () {
        return this.intErrorText
    },
    setDateErrorText: function ($) {
        this.dateErrorText = $
    },
    getDateErrorText: function () {
        return this.dateErrorText
    },
    setMaxLengthErrorText: function ($) {
        this.maxLengthErrorText = $
    },
    getMaxLengthErrorText: function () {
        return this.maxLengthErrorText
    },
    setMinLengthErrorText: function ($) {
        this.minLengthErrorText = $
    },
    getMinLengthErrorText: function () {
        return this.minLengthErrorText
    },
    setMaxErrorText: function ($) {
        this.maxErrorText = $
    },
    getMaxErrorText: function () {
        return this.maxErrorText
    },
    setMinErrorText: function ($) {
        this.minErrorText = $
    },
    getMinErrorText: function () {
        return this.minErrorText
    },
    setRangeLengthErrorText: function ($) {
        this.rangeLengthErrorText = $
    },
    getRangeLengthErrorText: function () {
        return this.rangeLengthErrorText
    },
    setRangeCharErrorText: function ($) {
        this.rangeCharErrorText = $
    },
    getRangeCharErrorText: function () {
        return this.rangeCharErrorText
    },
    setRangeErrorText: function ($) {
        this.rangeErrorText = $
    },
    getRangeErrorText: function () {
        return this.rangeErrorText
    }
});
HbyG(mini.TextBox, "textbox");
mini.Password = function () {
    mini.Password["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Password, mini.TextBox, {
    uiCls: "mini-password",
    $wSe: "password",
    setEmptyText: function ($) {
        this["emptyText"] = ""
    }
});
HbyG(mini.Password, "password");
mini.TextArea = function () {
    mini.TextArea["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.TextArea, mini.TextBox, {
    maxLength: 100000,
    width: 180,
    height: 50,
    minHeight: 50,
    $wSe: "textarea",
    uiCls: "mini-textarea",
    doLayout: function () {
        if (!this.canLayout()) return;
        mini.TextArea["superclass"]["doLayout"]["call"](this);
        var $ = Nf(this.el);
        $ -= 2;
        if ($ < 0) $ = 0;
        this.VXt.style.height = $ + "px"
    }
});
HbyG(mini.TextArea, "textarea");
mini.PopupEdit = function () {
    mini.PopupEdit["superclass"]["constructor"]["call"](this);
    this.PLWQ();
    this.el.className += " mini-popupedit"
};
Pv_r(mini.PopupEdit, mini.ButtonEdit, {
    uiCls: "mini-popupedit",
    popup: null,
    popupCls: "mini-buttonedit-popup",
    _hoverCls: "mini-buttonedit-hover",
    ZyR: "mini-buttonedit-pressed",
    destroy: function ($) {
        if (this.isShowPopup()) this["hidePopup"]();
        if (this.popup) {
            this.popup["destroy"]();
            this.popup = null
        }
        mini.PopupEdit["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        mini.PopupEdit["superclass"]["_initEvents"]["call"](this);
        KJ_T(function () {
            _pS(this.el, "mouseover", this.Vv5, this);
            _pS(this.el, "mouseout", this.Gj$, this)
        },
		this)
    },
    DEv3: function () {
        this.buttons = [];
        var $ = this.createButton({
            cls: "mini-buttonedit-popup",
            iconCls: "mini-buttonedit-icons-popup",
            name: "popup"
        });
        this.buttons.push($)
    },
    Vv5: function ($) {
        if (this["isReadOnly"]() || this.allowInput) return;
        if (MRj9($.target, "mini-buttonedit-border")) this["addCls"](this._hoverCls)
    },
    Gj$: function ($) {
        if (this["isReadOnly"]() || this.allowInput) return;
        this["removeCls"](this._hoverCls)
    },
    UNAE: function ($) {
        if (this["isReadOnly"]()) return;
        mini.PopupEdit["superclass"].UNAE["call"](this, $);
        if (this.allowInput == false && MRj9($.target, "mini-buttonedit-border")) {
            RE(this.el, this.ZyR);
            VNV(document, "mouseup", this.NkrJ, this)
        }
    },
    $Qvl: function ($) {
        this.fire("keydown", {
            htmlEvent: $
        });
        if ($.keyCode == 8 && (this["isReadOnly"]() || this.allowInput == false)) return false;
        if ($.keyCode == 9) {
            this["hidePopup"]();
            return
        }
        if ($.keyCode == 27) {
            this["hidePopup"]();
            return
        }
        if ($.keyCode == 13) this.fire("enter");
        if (this.isShowPopup()) if ($.keyCode == 13 || $.keyCode == 27) $.stopPropagation()
    },
    within: function ($) {
        if (Yma(this.el, $.target)) return true;
        if (this.popup["within"]($)) return true;
        return false
    },
    popupWidth: "100%",
    popupMinWidth: 50,
    popupMaxWidth: 2000,
    popupHeight: "",
    popupMinHeight: 30,
    popupMaxHeight: 2000,
    setPopup: function ($) {
        if (typeof $ == "string") {
            mini.parse($);
            $ = mini.get($)
        }
        var _ = mini.getAndCreate($);
        if (!_) return;
        _["setVisible"](true);
        _["render"](this.popup._contentEl);
        _.owner = this;
        _.on("beforebuttonclick", this.Y9P, this)
    },
    getPopup: function () {
        if (!this.popup) this.PLWQ();
        return this.popup
    },
    PLWQ: function () {
        this.popup = new mini.Popup();
        this.popup.setShowAction("none");
        this.popup.setHideAction("outerclick");
        this.popup.setPopupEl(this.el);
        this.popup.on("BeforeClose", this.WD2, this);
        VNV(this.popup.el, "keydown", this.__OnPopupKeyDown, this)
    },
    WD2: function ($) {
        if (this["within"]($.htmlEvent)) $.cancel = true
    },
    __OnPopupKeyDown: function ($) { },
    showPopup: function () {
        var _ = this.getPopup(),
		B = this.getBox(),
		$ = this["popupWidth"];
        if (this["popupWidth"] == "100%") $ = B.width;
        _["setWidth"]($);
        var A = parseInt(this["popupHeight"]);
        if (!isNaN(A)) _["setHeight"](A);
        else _["setHeight"]("auto");
        _.setMinWidth(this["popupMinWidth"]);
        _.setMinHeight(this["popupMinHeight"]);
        _.setMaxWidth(this["popupMaxWidth"]);
        _.setMaxHeight(this["popupMaxHeight"]);
        _.showAtEl(this.el, {
            hAlign: "left",
            vAlign: "below",
            outVAlign: "above",
            outHAlign: "right",
            popupCls: this.popupCls
        });
        _.on("Close", this.LNH, this);
        this.fire("showpopup")
    },
    LNH: function ($) {
        this.fire("hidepopup")
    },
    hidePopup: function () {
        var $ = this.getPopup();
        $.close()
    },
    isShowPopup: function () {
        if (this.popup && this.popup.visible) return true;
        else return false
    },
    setPopupWidth: function ($) {
        this["popupWidth"] = $
    },
    setPopupMaxWidth: function ($) {
        this["popupMaxWidth"] = $
    },
    setPopupMinWidth: function ($) {
        this["popupMinWidth"] = $
    },
    getPopupWidth: function ($) {
        return this["popupWidth"]
    },
    getPopupMaxWidth: function ($) {
        return this["popupMaxWidth"]
    },
    getPopupMinWidth: function ($) {
        return this["popupMinWidth"]
    },
    setPopupHeight: function ($) {
        this["popupHeight"] = $
    },
    setPopupMaxHeight: function ($) {
        this["popupMaxHeight"] = $
    },
    setPopupMinHeight: function ($) {
        this["popupMinHeight"] = $
    },
    getPopupHeight: function ($) {
        return this["popupHeight"]
    },
    getPopupMaxHeight: function ($) {
        return this["popupMaxHeight"]
    },
    getPopupMinHeight: function ($) {
        return this["popupMinHeight"]
    },
    YY: function (_) {
        if (this["isReadOnly"]()) return;
        if (Yma(this._buttonEl, _.target)) this.QN(_);
        if (this.allowInput == false || Yma(this._buttonEl, _.target)) if (this.isShowPopup()) this["hidePopup"]();
        else {
            var $ = this;
            setTimeout(function () {
                $["showPopup"]()
            },
			1)
        }
    },
    Y9P: function ($) {
        if ($.name == "close") this["hidePopup"]();
        $.cancel = true
    },
    getAttrs: function ($) {
        var _ = mini.PopupEdit["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["popupWidth", "popupHeight", "popup", "onshowpopup", "onhidepopup"]);
        mini["_ParseInt"]($, _, ["popupMinWidth", "popupMaxWidth", "popupMinHeight", "popupMaxHeight"]);
        return _
    }
});
HbyG(mini.PopupEdit, "popupedit");
mini.ComboBox = function () {
    this.data = [];
    this.columns = [];
    mini.ComboBox["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.ComboBox, mini.PopupEdit, {
    text: "",
    value: "",
    valueField: "id",
    textField: "text",
    delimiter: ",",
    multiSelect: false,
    data: [],
    url: "",
    columns: [],
    allowInput: false,
    valueFromSelect: false,
    popupMaxHeight: 200,
    set: function (A) {
        if (typeof A == "string") return this;
        var $ = A.value;
        delete A.value;
        var B = A.url;
        delete A.url;
        var _ = A.data;
        delete A.data;
        mini.ComboBox["superclass"].set["call"](this, A);
        if (!mini.isNull(_)) {
            this["setData"](_);
            A.data = _
        }
        if (!mini.isNull(B)) {
            this.setUrl(B);
            A.url = B
        }
        if (!mini.isNull($)) {
            this["setValue"]($);
            A.value = $
        }
        return this
    },
    uiCls: "mini-combobox",
    PLWQ: function () {
        mini.ComboBox["superclass"].PLWQ["call"](this);
        this.QM = new mini.ListBox();
        this.QM.setBorderStyle("border:0;");
        this.QM.setStyle("width:100%;height:auto;");
        this.QM["render"](this.popup._contentEl);
        this.QM.on("itemclick", this.P3, this)
    },
    showPopup: function () {
        this.QM["setHeight"]("auto");
        mini.ComboBox["superclass"]["showPopup"]["call"](this);
        var $ = this.popup.el.style.height;
        if ($ == "" || $ == "auto") this.QM["setHeight"]("auto");
        else this.QM["setHeight"]("100%");
        this.QM["setValue"](this.value)
    },
    getItem: function ($) {
        return typeof $ == "object" ? $ : this.data[$]
    },
    indexOf: function ($) {
        return this.data.indexOf($)
    },
    getAt: function ($) {
        return this.data[$]
    },
    load: function ($) {
        if (typeof $ == "string") this.setUrl($);
        else this["setData"]($)
    },
    setData: function (data) {
        if (typeof data == "string") data = eval("(" + data + ")");
        if (!mini.isArray(data)) data = [];
        this.QM["setData"](data);
        this.data = this.QM.data;
        var vts = this.QM.B0$Z(this.value);
        this.VXt.value = vts[1]
    },
    getData: function () {
        return this.data
    },
    setUrl: function (_) {
        this.getPopup();
        this.QM.setUrl(_);
        this.url = this.QM.url;
        this.data = this.QM.data;
        var $ = this.QM.B0$Z(this.value);
        this.VXt.value = $[1]
    },
    getUrl: function () {
        return this.url
    },
    setValueField: function ($) {
        this["valueField"] = $;
        if (this.QM) this.QM["setValueField"]($)
    },
    getValueField: function () {
        return this["valueField"]
    },
    setTextField: function ($) {
        if (this.QM) this.QM.setTextField($);
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    setDisplayField: function ($) {
        this.setTextField($)
    },
    setValue: function ($) {
        if (this.value !== $) {
            var _ = this.QM.B0$Z($);
            this.value = $;
            this.Izde.value = this.value;
            this.VXt.value = _[1]
        } else {
            _ = this.QM.B0$Z($);
            this.VXt.value = _[1]
        }
    },
    setMultiSelect: function ($) {
        if (this["multiSelect"] != $) {
            this["multiSelect"] = $;
            if (this.QM) {
                this.QM.setMultiSelect($);
                this.QM.setShowCheckBox($)
            }
        }
    },
    getMultiSelect: function () {
        return this["multiSelect"]
    },
    setColumns: function ($) {
        if (!mini.isArray($)) $ = [];
        this.columns = $;
        this.QM["setColumns"]($)
    },
    getColumns: function () {
        return this.columns
    },
    showNullItem: false,
    setShowNullItem: function ($) {
        if (this.showNullItem != $) {
            this.showNullItem = $;
            this.QM.setShowNullItem($)
        }
    },
    getShowNullItem: function () {
        return this.showNullItem
    },
    setValueFromSelect: function ($) {
        this.valueFromSelect = $
    },
    getValueFromSelect: function () {
        return this.valueFromSelect
    },
    QWeD: function () {
        if (this.validateOnChanged) this["validate"]();
        var $ = this.getValue(),
		B = this.getSelecteds(),
		_ = B[0],
		A = this;
        A.fire("valuechanged", {
            value: $,
            selecteds: B,
            selected: _
        })
    },
    getSelecteds: function () {
        return this.QM.findItems(this.value)
    },
    getSelected: function () {
        return this.getSelecteds()[0]
    },
    P3: function (C) {
        var B = this.QM.getValue(),
		A = this.QM.B0$Z(B),
		$ = this.getValue();
        this["setValue"](B);
        this["setText"](A[1]);
        if ($ != this.getValue()) {
            var _ = this;
            setTimeout(function () {
                _.QWeD()
            },
			1)
        }
        if (!this["multiSelect"]) this["hidePopup"]();
        this.focus()
    },
    $Qvl: function (B) {
        this.fire("keydown", {
            htmlEvent: B
        });
        if (B.keyCode == 8 && (this["isReadOnly"]() || this.allowInput == false)) return false;
        if (B.keyCode == 9) {
            this["hidePopup"]();
            return
        }
        switch (B.keyCode) {
            case 27:
                if (this.isShowPopup()) B.stopPropagation();
                this["hidePopup"]();
                break;
            case 13:
                if (this.isShowPopup()) {
                    B.preventDefault();
                    B.stopPropagation();
                    var _ = this.QM.getFocusedIndex();
                    if (_ != -1) {
                        var $ = this.QM.getAt(_),
					A = this.QM.B0$Z([$]);
                        this["setValue"](A[0]);
                        this["setText"](A[1]);
                        this.QWeD();
                        this["hidePopup"]()
                    }
                } else this.fire("enter");
                break;
            case 37:
                break;
            case 38:
                _ = this.QM.getFocusedIndex();
                if (_ == -1) {
                    _ = 0;
                    if (!this["multiSelect"]) {
                        $ = this.QM.findItems(this.value)[0];
                        if ($) _ = this.QM.indexOf($)
                    }
                }
                if (this.isShowPopup()) if (!this["multiSelect"]) {
                    _ -= 1;
                    if (_ < 0) _ = 0;
                    this.QM.M9(_, true)
                }
                break;
            case 39:
                break;
            case 40:
                _ = this.QM.getFocusedIndex();
                if (_ == -1) {
                    _ = 0;
                    if (!this["multiSelect"]) {
                        $ = this.QM.findItems(this.value)[0];
                        if ($) _ = this.QM.indexOf($)
                    }
                }
                if (this.isShowPopup()) {
                    if (!this["multiSelect"]) {
                        _ += 1;
                        if (_ > this.QM.getCount() - 1) _ = this.QM.getCount() - 1;
                        this.QM.M9(_, true)
                    }
                } else {
                    this["showPopup"]();
                    if (!this["multiSelect"]) this.QM.M9(_, true)
                }
                break;
            default:
                this.F1OV(this.VXt.value);
                break
        }
    },
    Zjo: function ($) {
        this.fire("keyup", {
            htmlEvent: $
        })
    },
    L2Z: function ($) {
        this.fire("keypress", {
            htmlEvent: $
        })
    },
    F1OV: function (_) {
        var $ = this;
        setTimeout(function () {
            var A = $.VXt.value;
            if (A != _) $.$lun(A)
        },
		10)
    },
    $lun: function (B) {
        if (this["multiSelect"] == true) return;
        var A = [];
        for (var C = 0, E = this.data.length; C < E; C++) {
            var _ = this.data[C],
			D = _[this.textField];
            if (typeof D == "string") if (D.indexOf(B) != -1) A.push(_)
        }
        this.QM["setData"](A);
        this._filtered = true;
        if (B !== "" || this.isShowPopup()) {
            this["showPopup"]();
            var $ = 0;
            if (this.QM.getShowNullItem()) $ = 1;
            this.QM.M9($, true)
        }
    },
    LNH: function ($) {
        if (this._filtered) {
            this._filtered = false;
            if (this.QM.el) this.QM["setData"](this.data)
        }
        this.fire("hidepopup")
    },
    DvF: function (J) {
        if (this["multiSelect"] == false) {
            var E = this.VXt.value;
            if (this.valueFromSelect == false) {
                this["setValue"](E);
                if (this.value && !this.VXt.value) this["setText"](E);
                this.QWeD()
            } else {
                var H = this.getData(),
				F = null;
                for (var D = 0, B = H.length; D < B; D++) {
                    var $ = H[D],
					I = $[this.textField];
                    if (I == E) {
                        F = $;
                        break
                    }
                }
                this.QM["setValue"](F ? F[this.valueField] : "");
                var C = this.QM.getValue(),
				A = this.QM.B0$Z(C),
				_ = this.getValue();
                this["setValue"](C);
                this["setText"](A[1]);
                if (_ != this.getValue()) {
                    var G = this;
                    G.QWeD()
                }
            }
        }
    },
    getAttrs: function (G) {
        var E = mini.ComboBox["superclass"]["getAttrs"]["call"](this, G);
        mini["_ParseString"](G, E, ["url", "data", "textField", "valueField", "displayField"]);
        mini["_ParseBool"](G, E, ["multiSelect", "showNullItem", "valueFromSelect"]);
        if (E.displayField) E["textField"] = E.displayField;
        var C = E["valueField"] || this["valueField"],
		H = E["textField"] || this["textField"];
        if (G.nodeName.toLowerCase() == "select") {
            var I = [];
            for (var F = 0, D = G.length; F < D; F++) {
                var $ = G.options[F],
				_ = {};
                _[H] = $.text;
                _[C] = $.value;
                I.push(_)
            }
            if (I.length > 0) E.data = I
        } else {
            var J = mini["getChildNodes"](G);
            for (F = 0, D = J.length; F < D; F++) {
                var A = J[F],
				B = jQuery(A).attr("property");
                if (!B) continue;
                B = B.toLowerCase();
                if (B == "columns") E.columns = mini._ParseColumns(A);
                else if (B == "data") E.data = A.innerHTML
            }
        }
        return E
    }
});
HbyG(mini.ComboBox, "combobox");
mini.DatePicker = function () {
    mini.DatePicker["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.DatePicker, mini.PopupEdit, {
    format: "yyyy-MM-dd",
    popupWidth: "",
    viewDate: new Date(),
    showTime: false,
    timeFormat: "H:mm",
    showTodayButton: true,
    showClearButton: true,
    uiCls: "mini-datepicker",
    _getCalendar: function () {
        if (!mini.DatePicker._Calendar) {
            var $ = mini.DatePicker._Calendar = new mini.Calendar();
            $.setStyle("border:0;")
        }
        return mini.DatePicker._Calendar
    },
    PLWQ: function () {
        mini.DatePicker["superclass"].PLWQ["call"](this);
        this.Z6 = this._getCalendar()
    },
    showPopup: function () {
        this.Z6.beginUpdate();
        this.Z6["render"](this.popup._contentEl);
        this.Z6.set({
            showTime: this.showTime,
            timeFormat: this.timeFormat,
            showClearButton: this.showClearButton,
            showTodayButton: this.showTodayButton
        });
        this.Z6["setValue"](this.value);
        if (this.value) this.Z6.setViewDate(this.value);
        else this.Z6.setViewDate(this.viewDate);
        if (this.Z6._target) {
            var $ = this.Z6._target;
            this.Z6.un("timechanged", $.NQ, $);
            this.Z6.un("dateclick", $.MPB, $);
            this.Z6.un("drawdate", $.YCu, $)
        }
        this.Z6.on("timechanged", this.NQ, this);
        this.Z6.on("dateclick", this.MPB, this);
        this.Z6.on("drawdate", this.YCu, this);
        this.Z6.endUpdate();
        mini.DatePicker["superclass"]["showPopup"]["call"](this);
        this.Z6._target = this;
        this.Z6.focus()
    },
    hidePopup: function () {
        mini.DatePicker["superclass"]["hidePopup"]["call"](this);
        this.Z6.un("timechanged", this.NQ, this);
        this.Z6.un("dateclick", this.MPB, this);
        this.Z6.un("drawdate", this.YCu, this)
    },
    within: function ($) {
        if (Yma(this.el, $.target)) return true;
        if (this.Z6["within"]($)) return true;
        return false
    },
    __OnPopupKeyDown: function ($) {
        if ($.keyCode == 13) this.MPB();
        if ($.keyCode == 27) {
            this["hidePopup"]();
            this.focus()
        }
    },
    YCu: function ($) {
        this.fire("drawdate", $)
    },
    MPB: function (A) {
        var _ = this.Z6.getValue(),
		$ = this.getFormValue();
        this["setValue"](_);
        if ($ !== this.getFormValue()) this.QWeD();
        this.focus();
        this["hidePopup"]()
    },
    NQ: function (_) {
        var $ = this.Z6.getValue();
        this["setValue"]($);
        this.QWeD()
    },
    setFormat: function ($) {
        if (typeof $ != "string") return;
        if (this.format != $) {
            this.format = $;
            this.VXt.value = this.Izde.value = this.getFormValue()
        }
    },
    setValue: function ($) {
        $ = mini.parseDate($);
        if (mini.isNull($)) $ = "";
        if (mini.isDate($)) $ = new Date($.getTime());
        if (this.value != $) {
            this.value = $;
            this.VXt.value = this.Izde.value = this.getFormValue()
        }
    },
    getValue: function () {
        if (!mini.isDate(this.value)) return null;
        return this.value
    },
    getFormValue: function () {
        if (!mini.isDate(this.value)) return "";
        return mini.formatDate(this.value, this.format)
    },
    setViewDate: function ($) {
        $ = mini.parseDate($);
        if (!mini.isDate($)) return;
        this.viewDate = $
    },
    getViewDate: function () {
        return this.Z6.getViewDate()
    },
    setShowTime: function ($) {
        if (this.showTime != $) this.showTime = $
    },
    getShowTime: function () {
        return this.showTime
    },
    setTimeFormat: function ($) {
        if (this.timeFormat != $) this.timeFormat = $
    },
    getTimeFormat: function () {
        return this.timeFormat
    },
    setShowTodayButton: function ($) {
        this.showTodayButton = $
    },
    getShowTodayButton: function () {
        return this.showTodayButton
    },
    setShowClearButton: function ($) {
        this.showClearButton = $
    },
    getShowClearButton: function () {
        return this.showClearButton
    },
    DvF: function (B) {
        var A = this.VXt.value,
		$ = mini.parseDate(A);
        if (!$ || isNaN($) || $.getFullYear() == 1970) $ = null;
        var _ = this.getFormValue();
        this["setValue"]($);
        if ($ == null) this.VXt.value = "";
        if (_ !== this.getFormValue()) this.QWeD()
    },
    $Qvl: function (_) {
        this.fire("keydown", {
            htmlEvent: _
        });
        if (_.keyCode == 8 && (this["isReadOnly"]() || this.allowInput == false)) return false;
        if (_.keyCode == 9) {
            this["hidePopup"]();
            return
        }
        switch (_.keyCode) {
            case 27:
                if (this.isShowPopup()) _.stopPropagation();
                this["hidePopup"]();
                break;
            case 13:
                if (this.isShowPopup()) {
                    _.preventDefault();
                    _.stopPropagation();
                    this["hidePopup"]()
                } else {
                    this.DvF(null);
                    var $ = this;
                    setTimeout(function () {
                        $.fire("enter")
                    },
				10)
                }
                break;
            case 37:
                break;
            case 38:
                _.preventDefault();
                break;
            case 39:
                break;
            case 40:
                _.preventDefault();
                this["showPopup"]();
                break;
            default:
                break
        }
    },
    getAttrs: function ($) {
        var _ = mini.DatePicker["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["format", "viewDate", "timeFormat", "ondrawdate"]);
        mini["_ParseBool"]($, _, ["showTime", "showTodayButton", "showClearButton"]);
        return _
    }
});
HbyG(mini.DatePicker, "datepicker");
mini.Calendar = function () {
    this.viewDate = new Date(),
	this.KfE6 = [];
    mini.Calendar["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Calendar, mini.Control, {
    width: 220,
    height: 160,
    _clearBorder: false,
    viewDate: null,
    Iy4Z: "",
    KfE6: [],
    multiSelect: false,
    firstDayOfWeek: 0,
    todayText: "Today",
    clearText: "Clear",
    okText: "OK",
    cancelText: "Cancel",
    daysShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
    format: "MMM,yyyy",
    timeFormat: "H:mm",
    showTime: false,
    currentTime: true,
    rows: 1,
    columns: 1,
    headerCls: "",
    bodyCls: "",
    footerCls: "",
    WEv: "mini-calendar-today",
    NPi9: "mini-calendar-weekend",
    ZmP: "mini-calendar-othermonth",
    Mbi: "mini-calendar-selected",
    showHeader: true,
    showFooter: true,
    showWeekNumber: false,
    showDaysHeader: true,
    showMonthButtons: true,
    showYearButtons: true,
    showTodayButton: true,
    showClearButton: true,
    isWeekend: function (_) {
        var $ = _.getDay();
        return $ == 0 || $ == 6
    },
    getFirstDateOfMonth: function ($) {
        var $ = new Date($.getFullYear(), $.getMonth(), 1);
        return mini.getWeekStartDate($, this.firstDayOfWeek)
    },
    getShortWeek: function ($) {
        return this.daysShort[$]
    },
    uiCls: "mini-calendar",
    _create: function () {
        var C = "<tr style=\"width:100%;\"><td style=\"width:100%;\"></td></tr>";
        C += "<tr ><td><div class=\"mini-calendar-footer\">" + "<span style=\"display:inline-block;\"><input name=\"time\" class=\"mini-timespinner\" style=\"width:80px\" format=\"" + this.timeFormat + "\"/>" + "<span class=\"mini-calendar-footerSpace\"></span></span>" + "<span class=\"mini-calendar-tadayButton\">" + this.todayText + "</span>" + "<span class=\"mini-calendar-footerSpace\"></span>" + "<span class=\"mini-calendar-clearButton\">" + this.clearText + "</span>" + "<a href=\"#\" class=\"mini-calendar-focus\" style=\"position:absolute;left:-10px;top:-10px;width:0px;height:0px;outline:none\" hideFocus></a>" + "</div></td></tr>";
        var A = "<table class=\"mini-calendar\" cellpadding=\"0\" cellspacing=\"0\">" + C + "</table>",
		_ = document.createElement("div");
        _.innerHTML = A;
        this.el = _.firstChild;
        var $ = this.el.getElementsByTagName("tr"),
		B = this.el.getElementsByTagName("td");
        this.N$i = B[0];
        this.Aca = mini.byClass("mini-calendar-footer", this.el);
        this.timeWrapEl = this.Aca.childNodes[0];
        this.todayButtonEl = this.Aca.childNodes[1];
        this.footerSpaceEl = this.Aca.childNodes[2];
        this.closeButtonEl = this.Aca.childNodes[3];
        this._focusEl = this.Aca.lastChild;
        mini.parse(this.Aca);
        this.timeSpinner = mini.getbyName("time", this.el);
        this["doUpdate"]()
    },
    focus: function () {
        try {
            this._focusEl.focus()
        } catch ($) { }
    },
    destroy: function ($) {
        this.N$i = this.Aca = this.timeWrapEl = this.todayButtonEl = this.footerSpaceEl = this.closeButtonEl = null;
        mini.Calendar["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        if (this.timeSpinner) this.timeSpinner.on("valuechanged", this.NQ, this);
        KJ_T(function () {
            VNV(this.el, "click", this.YY, this);
            VNV(this.el, "mousedown", this.UNAE, this);
            VNV(this.el, "keydown", this.M$, this)
        },
		this)
    },
    getDateEl: function ($) {
        if (!$) return null;
        var _ = this.uid + "$" + mini.clearTime($).getTime();
        return document.getElementById(_)
    },
    within: function ($) {
        if (Yma(this.el, $.target)) return true;
        if (this.menuEl && Yma(this.menuEl, $.target)) return true;
        return false
    },
    setShowClearButton: function ($) {
        this.showClearButton = $;
        var _ = this.getButton("clear");
        if (_) this["doUpdate"]()
    },
    getShowClearButton: function () {
        return this.showClearButton
    },
    setShowHeader: function ($) {
        this.showHeader = $;
        this["doUpdate"]()
    },
    getShowHeader: function () {
        return this.showHeader
    },
    setShowFooter: function ($) {
        this["showFooter"] = $;
        this["doUpdate"]()
    },
    getShowFooter: function () {
        return this["showFooter"]
    },
    setShowWeekNumber: function ($) {
        this.showWeekNumber = $;
        this["doUpdate"]()
    },
    getShowWeekNumber: function () {
        return this.showWeekNumber
    },
    setShowDaysHeader: function ($) {
        this.showDaysHeader = $;
        this["doUpdate"]()
    },
    getShowDaysHeader: function () {
        return this.showDaysHeader
    },
    setShowMonthButtons: function ($) {
        this.showMonthButtons = $;
        this["doUpdate"]()
    },
    getShowMonthButtons: function () {
        return this.showMonthButtons
    },
    setShowYearButtons: function ($) {
        this.showYearButtons = $;
        this["doUpdate"]()
    },
    getShowYearButtons: function () {
        return this.showYearButtons
    },
    setShowTodayButton: function ($) {
        this.showTodayButton = $;
        this["doUpdate"]()
    },
    getShowTodayButton: function () {
        return this.showTodayButton
    },
    setShowClearButton: function ($) {
        this.showClearButton = $;
        this["doUpdate"]()
    },
    getShowClearButton: function () {
        return this.showClearButton
    },
    setViewDate: function ($) {
        if (!$) $ = new Date();
        if (mini.isDate($)) $ = new Date($.getTime());
        this.viewDate = $;
        this["doUpdate"]()
    },
    getViewDate: function () {
        return this.viewDate
    },
    setSelectedDate: function ($) {
        $ = mini.parseDate($);
        if (!mini.isDate($)) $ = "";
        else $ = new Date($.getTime());
        var _ = this.getDateEl(this.Iy4Z);
        if (_) WeL(_, this.Mbi);
        this.Iy4Z = $;
        if (this.Iy4Z) this.Iy4Z = mini.cloneDate(this.Iy4Z);
        _ = this.getDateEl(this.Iy4Z);
        if (_) RE(_, this.Mbi);
        this.fire("datechanged")
    },
    setSelectedDates: function ($) {
        if (!mini.isArray($)) $ = [];
        this.KfE6 = $;
        this["doUpdate"]()
    },
    getSelectedDate: function () {
        return this.Iy4Z ? this.Iy4Z : ""
    },
    setTime: function ($) {
        this.timeSpinner["setValue"]($)
    },
    getTime: function () {
        return this.timeSpinner.getFormValue()
    },
    setValue: function ($) {
        this.setSelectedDate($);
        if (!$) $ = new Date();
        this.setTime($)
    },
    getValue: function () {
        var $ = this.Iy4Z;
        if ($) {
            $ = mini.clearTime($);
            if (this.showTime) {
                var _ = this.timeSpinner.getValue();
                $.setHours(_.getHours());
                $.setMinutes(_.getMinutes());
                $.setSeconds(_.getSeconds())
            }
        }
        return $ ? $ : ""
    },
    getFormValue: function () {
        var $ = this.getValue();
        if ($) return mini.formatDate($, "yyyy-MM-dd HH:mm:ss");
        return ""
    },
    isSelectedDate: function ($) {
        if (!$ || !this.Iy4Z) return false;
        return mini.clearTime($).getTime() == mini.clearTime(this.Iy4Z).getTime()
    },
    setMultiSelect: function ($) {
        this["multiSelect"] = $;
        this["doUpdate"]()
    },
    getMultiSelect: function () {
        return this["multiSelect"]
    },
    setRows: function ($) {
        if (isNaN($)) return;
        if ($ < 1) $ = 1;
        this.rows = $;
        this["doUpdate"]()
    },
    getRows: function () {
        return this.rows
    },
    setColumns: function ($) {
        if (isNaN($)) return;
        if ($ < 1) $ = 1;
        this.columns = $;
        this["doUpdate"]()
    },
    getColumns: function () {
        return this.columns
    },
    setShowTime: function ($) {
        if (this.showTime != $) {
            this.showTime = $;
            this["doLayout"]()
        }
    },
    getShowTime: function () {
        return this.showTime
    },
    setTimeFormat: function ($) {
        if (this.timeFormat != $) {
            this.timeSpinner.setFormat($);
            this.timeFormat = this.timeSpinner.format
        }
    },
    getTimeFormat: function () {
        return this.timeFormat
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        this.timeWrapEl.style.display = this.showTime ? "" : "none";
        this.todayButtonEl.style.display = this.showTodayButton ? "" : "none";
        this.closeButtonEl.style.display = this.showClearButton ? "" : "none";
        this.footerSpaceEl.style.display = (this.showClearButton && this.showTodayButton) ? "" : "none";
        this.Aca.style.display = this["showFooter"] ? "" : "none";
        var _ = this.N$i.firstChild,
		$ = this["isAutoHeight"]();
        if (!$) {
            _.parentNode.style.height = "100px";
            h = jQuery(this.el).height();
            h -= jQuery(this.Aca).outerHeight();
            _.parentNode.style.height = h + "px"
        } else _.parentNode.style.height = "";
        mini.layout(this.Aca)
    },
    doUpdate: function () {
        if (!this.N2b) return;
        var F = new Date(this.viewDate.getTime()),
		A = this.rows == 1 && this.columns == 1,
		B = 100 / this.rows,
		E = "<table class=\"mini-calendar-views\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">";
        for (var $ = 0, D = this.rows; $ < D; $++) {
            E += "<tr >";
            for (var C = 0, _ = this.columns; C < _; C++) {
                E += "<td style=\"height:" + B + "%\">";
                E += this.BIN(F, $, C);
                E += "</td>";
                F = new Date(F.getFullYear(), F.getMonth() + 1, 1)
            }
            E += "</tr>"
        }
        E += "</table>";
        this.N$i.innerHTML = E;
        mini["repaint"](this.el);
        this["doLayout"]()
    },
    BIN: function (R, J, C) {
        var _ = R.getMonth(),
		F = this.getFirstDateOfMonth(R),
		K = new Date(F.getTime()),
		A = mini.clearTime(new Date()).getTime(),
		D = this.value ? mini.clearTime(this.value).getTime() : -1,
		N = this.rows > 1 || this.columns > 1,
		P = "";
        P += "<table class=\"mini-calendar-view\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">";
        if (this.showHeader) {
            P += "<tr ><td colSpan=\"10\" class=\"mini-calendar-header\"><div class=\"mini-calendar-headerInner\">";
            if (J == 0 && C == 0) {
                P += "<div class=\"mini-calendar-prev\">";
                if (this.showYearButtons) P += "<span class=\"mini-calendar-yearPrev\"></span>";
                if (this.showMonthButtons) P += "<span class=\"mini-calendar-monthPrev\"></span>";
                P += "</div>"
            }
            if (J == 0 && C == this.columns - 1) {
                P += "<div class=\"mini-calendar-next\">";
                if (this.showMonthButtons) P += "<span class=\"mini-calendar-monthNext\"></span>";
                if (this.showYearButtons) P += "<span class=\"mini-calendar-yearNext\"></span>";
                P += "</div>"
            }
            P += "<span class=\"mini-calendar-title\">" + mini.formatDate(R, this.format); +"</span>";
            P += "</div></td></tr>"
        }
        P += "<tr class=\"mini-calendar-daysheader\"><td class=\"mini-calendar-space\"></td>";
        if (this.showWeekNumber) P += "<td sclass=\"mini-calendar-weeknumber\"></td>";
        for (var L = this.firstDayOfWeek, B = L + 7; L < B; L++) {
            var O = this.getShortWeek(L);
            P += "<td valign=\"middle\">";
            P += O;
            P += "</td>";
            F = new Date(F.getFullYear(), F.getMonth(), F.getDate() + 1)
        }
        P += "<td class=\"mini-calendar-space\"></td></tr>";
        F = K;
        for (var H = 0; H <= 5; H++) {
            P += "<tr class=\"mini-calendar-days\"><td class=\"mini-calendar-space\"></td>";
            if (this.showWeekNumber) {
                var G = mini.getWeek(F.getFullYear(), F.getMonth() + 1, F.getDate());
                if (String(G).length == 1) G = "0" + G;
                P += "<td class=\"mini-calendar-weeknumber\" valign=\"middle\">" + G + "</td>"
            }
            for (L = this.firstDayOfWeek, B = L + 7; L < B; L++) {
                var M = this.isWeekend(F),
				I = mini.clearTime(F).getTime(),
				$ = I == A,
				E = this.isSelectedDate(F);
                if (_ != F.getMonth() && N) I = -1;
                var Q = this.ESt(F);
                P += "<td valign=\"middle\" id=\"";
                P += this.uid + "$" + I;
                P += "\" class=\"mini-calendar-date ";
                if (M) P += " mini-calendar-weekend ";
                if (Q["allowSelect"] == false) P += " mini-calendar-disabled ";
                if (_ != F.getMonth() && N);
                else {
                    if (E) P += " " + this.Mbi + " ";
                    if ($) P += " mini-calendar-today "
                }
                if (_ != F.getMonth()) P += " mini-calendar-othermonth ";
                P += "\">";
                if (_ != F.getMonth() && N);
                else P += Q.dateHtml;
                P += "</td>";
                F = new Date(F.getFullYear(), F.getMonth(), F.getDate() + 1)
            }
            P += "<td class=\"mini-calendar-space\"></td></tr>"
        }
        P += "<tr class=\"mini-calendar-bottom\" colSpan=\"10\"><td ></td></tr>";
        P += "</table>";
        return P
    },
    ESt: function ($) {
        var _ = {
            date: $,
            dateCls: "",
            dateStyle: "",
            dateHtml: $.getDate(),
            allowSelect: true
        };
        this.fire("drawdate", _);
        return _
    },
    Mj: function (_, $) {
        var A = {
            date: _,
            action: $
        };
        this.fire("dateclick", A);
        this.QWeD()
    },
    menuEl: null,
    menuYear: null,
    menuSelectMonth: null,
    menuSelectYear: null,
    showMenu: function (_) {
        if (!_) return;
        this.hideMenu();
        this.menuYear = parseInt(this.viewDate.getFullYear() / 10) * 10;
        this.V3electMonth = this.viewDate.getMonth();
        this.V3electYear = this.viewDate.getFullYear();
        var A = "<div class=\"mini-calendar-menu\"></div>";
        this.menuEl = mini.append(document.body, A);
        this.updateMenu(this.viewDate);
        var $ = this.getBox();
        if (this.el.style.borderWidth == "0px") this.menuEl.style.border = "0";
        Ggi(this.menuEl, $);
        VNV(this.menuEl, "click", this.Q07, this);
        VNV(document, "mousedown", this.JbpU, this)
    },
    hideMenu: function () {
        if (this.menuEl) {
            BP(this.menuEl, "click", this.Q07, this);
            BP(document, "mousedown", this.JbpU, this);
            jQuery(this.menuEl).remove();
            this.menuEl = null
        }
    },
    updateMenu: function () {
        var C = "<div class=\"mini-calendar-menu-months\">";
        for (var $ = 0, B = 12; $ < B; $++) {
            var _ = mini.getShortMonth($),
			A = "";
            if (this.V3electMonth == $) A = "mini-calendar-menu-selected";
            C += "<a id=\"" + $ + "\" class=\"mini-calendar-menu-month " + A + "\" href=\"javascript:void(0);\" hideFocus onclick=\"return false\">" + _ + "</a>"
        }
        C += "<div style=\"clear:both;\"></div></div>";
        C += "<div class=\"mini-calendar-menu-years\">";
        for ($ = this.menuYear, B = this.menuYear + 10; $ < B; $++) {
            _ = $,
			A = "";
            if (this.V3electYear == $) A = "mini-calendar-menu-selected";
            C += "<a id=\"" + $ + "\" class=\"mini-calendar-menu-year " + A + "\" href=\"javascript:void(0);\" hideFocus onclick=\"return false\">" + _ + "</a>"
        }
        C += "<div class=\"mini-calendar-menu-prevYear\"></div><div class=\"mini-calendar-menu-nextYear\"></div><div style=\"clear:both;\"></div></div>";
        C += "<div class=\"mini-calendar-footer\">" + "<span class=\"mini-calendar-okButton\">" + this.okText + "</span>" + "<span class=\"mini-calendar-footerSpace\"></span>" + "<span class=\"mini-calendar-cancelButton\">" + this.cancelText + "</span>" + "</div><div style=\"clear:both;\"></div>";
        this.menuEl.innerHTML = C
    },
    Q07: function (C) {
        var _ = C.target,
		B = MRj9(_, "mini-calendar-menu-month"),
		$ = MRj9(_, "mini-calendar-menu-year");
        if (B) {
            this.V3electMonth = parseInt(B.id);
            this.updateMenu()
        } else if ($) {
            this.V3electYear = parseInt($.id);
            this.updateMenu()
        } else if (MRj9(_, "mini-calendar-menu-prevYear")) {
            this.menuYear = this.menuYear - 1;
            this.menuYear = parseInt(this.menuYear / 10) * 10;
            this.updateMenu()
        } else if (MRj9(_, "mini-calendar-menu-nextYear")) {
            this.menuYear = this.menuYear + 11;
            this.menuYear = parseInt(this.menuYear / 10) * 10;
            this.updateMenu()
        } else if (MRj9(_, "mini-calendar-okButton")) {
            var A = new Date(this.V3electYear, this.V3electMonth, 1);
            this.setViewDate(A);
            this.hideMenu()
        } else if (MRj9(_, "mini-calendar-cancelButton")) this.hideMenu()
    },
    JbpU: function ($) {
        if (!MRj9($.target, "mini-calendar-menu")) this.hideMenu()
    },
    YY: function (H) {
        var G = this.viewDate;
        if (this.enabled == false) return;
        var C = H.target,
		F = MRj9(H.target, "mini-calendar-title");
        if (MRj9(C, "mini-calendar-monthNext")) {
            G.setMonth(G.getMonth() + 1);
            this.setViewDate(G)
        } else if (MRj9(C, "mini-calendar-yearNext")) {
            G.setFullYear(G.getFullYear() + 1);
            this.setViewDate(G)
        } else if (MRj9(C, "mini-calendar-monthPrev")) {
            G.setMonth(G.getMonth() - 1);
            this.setViewDate(G)
        } else if (MRj9(C, "mini-calendar-yearPrev")) {
            G.setFullYear(G.getFullYear() - 1);
            this.setViewDate(G)
        } else if (MRj9(C, "mini-calendar-tadayButton")) {
            var _ = new Date();
            this.setViewDate(_);
            this.setSelectedDate(_);
            if (this.currentTime) {
                var $ = new Date();
                this.setTime($)
            }
            this.Mj(_, "today")
        } else if (MRj9(C, "mini-calendar-clearButton")) {
            this.setSelectedDate(null);
            this.setTime(null);
            this.Mj(null, "clear")
        } else if (F) this.showMenu(F);
        var E = MRj9(H.target, "mini-calendar-date");
        if (E && !D4ge(E, "mini-calendar-disabled")) {
            var A = E.id.split("$"),
			B = parseInt(A[A.length - 1]);
            if (B == -1) return;
            var D = new Date(B);
            this.Mj(D)
        }
    },
    UNAE: function (C) {
        if (this.enabled == false) return;
        var B = MRj9(C.target, "mini-calendar-date");
        if (B && !D4ge(B, "mini-calendar-disabled")) {
            var $ = B.id.split("$"),
			_ = parseInt($[$.length - 1]);
            if (_ == -1) return;
            var A = new Date(_);
            this.setSelectedDate(A)
        }
    },
    NQ: function ($) {
        this.fire("timechanged");
        this.QWeD()
    },
    M$: function (B) {
        if (this.enabled == false) return;
        var _ = this.getSelectedDate();
        if (!_) _ = new Date(this.viewDate.getTime());
        switch (B.keyCode) {
            case 27:
                break;
            case 13:
                break;
            case 37:
                _ = mini.addDate(_, -1, "D");
                break;
            case 38:
                _ = mini.addDate(_, -7, "D");
                break;
            case 39:
                _ = mini.addDate(_, 1, "D");
                break;
            case 40:
                _ = mini.addDate(_, 7, "D");
                break;
            default:
                break
        }
        var $ = this;
        if (_.getMonth() != $.viewDate.getMonth()) {
            $.setViewDate(mini.cloneDate(_));
            $.focus()
        }
        var A = this.getDateEl(_);
        if (A && D4ge(A, "mini-calendar-disabled")) return;
        $.setSelectedDate(_);
        if (B.keyCode == 37 || B.keyCode == 38 || B.keyCode == 39 || B.keyCode == 40) B.preventDefault()
    },
    QWeD: function () {
        this.fire("valuechanged")
    },
    getAttrs: function ($) {
        var _ = mini.Calendar["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["viewDate", "rows", "columns", "ondateclick", "ondrawdate", "ondatechanged", "timeFormat", "ontimechanged", "onvaluechanged"]);
        mini["_ParseBool"]($, _, ["multiSelect", "showHeader", "showFooter", "showWeekNumber", "showDaysHeader", "showMonthButtons", "showYearButtons", "showTodayButton", "showClearButton", "showTime"]);
        return _
    }
});
HbyG(mini.Calendar, "calendar");
mini.ListBox = function () {
    mini.ListBox["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.ListBox, mini.ListControl, {
    formField: true,
    width: 200,
    columns: null,
    columnWidth: 80,
    showNullItem: false,
    nullText: "",
    showEmpty: false,
    emptyText: "",
    showCheckBox: false,
    showAllCheckBox: true,
    multiSelect: false,
    YUz: "mini-listbox-item",
    Q13: "mini-listbox-item-hover",
    _Cm: "mini-listbox-item-selected",
    uiCls: "mini-listbox",
    _create: function () {
        var $ = this.el = document.createElement("div");
        this.el.className = "mini-listbox";
        this.el.innerHTML = "<div class=\"mini-listbox-border\"><div class=\"mini-listbox-header\"></div><div class=\"mini-listbox-view\"></div><input type=\"hidden\"/></div><div class=\"mini-errorIcon\"></div>";
        this._firstChild = this.el.firstChild;
        this.VEE = this._firstChild.firstChild;
        this.KyT0 = this._firstChild.childNodes[1];
        this.Izde = this._firstChild.childNodes[2];
        this.PMX = this.el.lastChild;
        this.Ku4 = this.KyT0
    },
    destroy: function ($) {
        if (this.KyT0) {
            mini["clearEvent"](this.KyT0);
            this.KyT0 = null
        }
        this._firstChild = null;
        this.VEE = null;
        this.KyT0 = null;
        this.Izde = null;
        mini.ListBox["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        mini.ListBox["superclass"]["_initEvents"]["call"](this);
        KJ_T(function () {
            _pS(this.KyT0, "scroll", this.AXlX, this)
        },
		this)
    },
    destroy: function ($) {
        if (this.KyT0) this.KyT0.onscroll = null;
        mini.ListBox["superclass"]["destroy"]["call"](this, $)
    },
    setColumns: function (_) {
        if (!mini.isArray(_)) _ = [];
        this.columns = _;
        for (var $ = 0, D = this.columns.length; $ < D; $++) {
            var B = this.columns[$];
            if (B.type) {
                if (!mini.isNull(B.header) && typeof B.header !== "function") if (B.header.trim() == "") delete B.header;
                var C = mini["_getColumn"](B.type);
                if (C) {
                    var E = mini.copyTo({},
					B);
                    mini.copyTo(B, C);
                    mini.copyTo(B, E)
                }
            }
            var A = parseInt(B.width);
            if (mini.isNumber(A) && String(A) == B.width) B.width = A + "px";
            if (mini.isNull(B.width)) B.width = this["columnWidth"] + "px"
        }
        this["doUpdate"]()
    },
    getColumns: function () {
        return this.columns
    },
    doUpdate: function () {
        if (this.N2b === false) return;
        var S = this.columns && this.columns.length > 0;
        if (S) RE(this.el, "mini-listbox-showColumns");
        else WeL(this.el, "mini-listbox-showColumns");
        this.VEE.style.display = S ? "" : "none";
        var I = [];
        if (S) {
            I[I.length] = "<table class=\"mini-listbox-headerInner\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
            var D = this.uid + "$ck$all";
            I[I.length] = "<td class=\"mini-listbox-checkbox\"><input type=\"checkbox\" id=\"" + D + "\"></td>";
            for (var R = 0, _ = this.columns.length; R < _; R++) {
                var B = this.columns[R],
				E = B.header;
                if (mini.isNull(E)) E = "&nbsp;";
                var A = B.width;
                if (mini.isNumber(A)) A = A + "px";
                I[I.length] = "<td class=\"";
                if (B.headerCls) I[I.length] = B.headerCls;
                I[I.length] = "\" style=\"";
                if (B.headerStyle) I[I.length] = B.headerStyle + ";";
                if (A) I[I.length] = "width:" + A + ";";
                if (B.headerAlign) I[I.length] = "text-align:" + B.headerAlign + ";";
                I[I.length] = "\">";
                I[I.length] = E;
                I[I.length] = "</td>"
            }
            I[I.length] = "</tr></table>"
        }
        this.VEE.innerHTML = I.join("");
        var I = [],
		P = this.data;
        I[I.length] = "<table class=\"mini-listbox-items\" cellspacing=\"0\" cellpadding=\"0\">";
        if (this["showEmpty"] && P.length == 0) I[I.length] = "<tr><td colspan=\"20\">" + this["emptyText"] + "</td></tr>";
        else {
            this.W75a();
            for (var K = 0, G = P.length; K < G; K++) {
                var $ = P[K],
				M = -1,
				O = " ",
				J = -1,
				N = " ";
                I[I.length] = "<tr id=\"";
                I[I.length] = this.PNIi(K);
                I[I.length] = "\" index=\"";
                I[I.length] = K;
                I[I.length] = "\" class=\"mini-listbox-item ";
                if ($.enabled === false) I[I.length] = " mini-disabled ";
                M = I.length;
                I[I.length] = O;
                I[I.length] = "\" style=\"";
                J = I.length;
                I[I.length] = N;
                I[I.length] = "\">";
                var H = this.HDA(K),
				L = this.name,
				F = this["getItemValue"]($),
				C = "";
                if ($.enabled === false) C = "disabled";
                I[I.length] = "<td class=\"mini-listbox-checkbox\"><input " + C + " id=\"" + H + "\" type=\"checkbox\" ></td>";
                if (S) {
                    for (R = 0, _ = this.columns.length; R < _; R++) {
                        var B = this.columns[R],
						T = this.Y2v($, K, B),
						A = B.width;
                        if (typeof A == "number") A = A + "px";
                        I[I.length] = "<td class=\"";
                        if (T.cellCls) I[I.length] = T.cellCls;
                        I[I.length] = "\" style=\"";
                        if (T.cellStyle) I[I.length] = T.cellStyle + ";";
                        if (A) I[I.length] = "width:" + A + ";";
                        if (B.align) I[I.length] = "text-align:" + B.align + ";";
                        I[I.length] = "\">";
                        I[I.length] = T.cellHtml;
                        I[I.length] = "</td>";
                        if (T.rowCls) O = T.rowCls;
                        if (T.rowStyle) N = T.rowStyle
                    }
                } else {
                    T = this.Y2v($, K, null);
                    I[I.length] = "<td class=\"";
                    if (T.cellCls) I[I.length] = T.cellCls;
                    I[I.length] = "\" style=\"";
                    if (T.cellStyle) I[I.length] = T.cellStyle;
                    I[I.length] = "\">";
                    I[I.length] = T.cellHtml;
                    I[I.length] = "</td>";
                    if (T.rowCls) O = T.rowCls;
                    if (T.rowStyle) N = T.rowStyle
                }
                I[M] = O;
                I[J] = N;
                I[I.length] = "</tr>"
            }
        }
        I[I.length] = "</table>";
        var Q = I.join("");
        this.KyT0.innerHTML = Q;
        this.YQU();
        this["doLayout"]()
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        if (this.columns && this.columns.length > 0) RE(this.el, "mini-listbox-showcolumns");
        else WeL(this.el, "mini-listbox-showcolumns");
        if (this["showCheckBox"]) WeL(this.el, "mini-listbox-hideCheckBox");
        else RE(this.el, "mini-listbox-hideCheckBox");
        var D = this.uid + "$ck$all",
		B = document.getElementById(D);
        if (B) B.style.display = this["showAllCheckBox"] ? "" : "none";
        var E = this["isAutoHeight"]();
        h = this["getHeight"](true);
        _ = this["getWidth"](true);
        var C = _,
		F = this.KyT0;
        F.style.width = _ + "px";
        if (!E) {
            var $ = Nf(this.VEE);
            h = h - $;
            F.style.height = h + "px"
        } else F.style.height = "auto";
        if (isIE) {
            var A = this.VEE.firstChild,
			G = this.KyT0.firstChild;
            if (this.KyT0.offsetHeight >= this.KyT0.scrollHeight) {
                G.style.width = "100%";
                if (A) A.style.width = "100%"
            } else {
                var _ = parseInt(G.parentNode.offsetWidth - 17) + "px";
                G.style.width = _;
                if (A) A.style.width = _
            }
        }
        if (this.KyT0.offsetHeight < this.KyT0.scrollHeight) this.VEE.style.width = (C - 17) + "px";
        else this.VEE.style.width = "100%"
    },
    setShowCheckBox: function ($) {
        this["showCheckBox"] = $;
        this["doLayout"]()
    },
    getShowCheckBox: function () {
        return this["showCheckBox"]
    },
    setShowAllCheckBox: function ($) {
        this["showAllCheckBox"] = $;
        this["doLayout"]()
    },
    getShowAllCheckBox: function () {
        return this["showAllCheckBox"]
    },
    setShowNullItem: function ($) {
        if (this.showNullItem != $) {
            this.showNullItem = $;
            this.W75a();
            this["doUpdate"]()
        }
    },
    getShowNullItem: function () {
        return this.showNullItem
    },
    W75a: function () {
        for (var _ = 0, A = this.data.length; _ < A; _++) {
            var $ = this.data[_];
            if ($.__NullItem) {
                this.data.removeAt(_);
                break
            }
        }
        if (this.showNullItem) {
            $ = {
                __NullItem: true
            };
            $[this.textField] = this.nullText;
            $[this.valueField] = "";
            this.data.insert(0, $)
        }
    },
    removeAll: function () {
        var $ = this.getData();
        this.removeItems($)
    },
    addItems: function (_, $) {
        if (!mini.isArray(_)) return;
        if (mini.isNull($)) $ = this.data.length;
        this.data.insertRange($, _);
        this["doUpdate"]()
    },
    addItem: function (_, $) {
        if (!_) return;
        if (this.data.indexOf(_) != -1) return;
        if (mini.isNull($)) $ = this.data.length;
        this.data.insert($, _);
        this["doUpdate"]()
    },
    removeItems: function ($) {
        if (!mini.isArray($)) return;
        this.data.removeRange($);
        this.JVD();
        this["doUpdate"]()
    },
    removeItem: function (_) {
        var $ = this.data.indexOf(_);
        if ($ != -1) {
            this.data.removeAt($);
            this.JVD();
            this["doUpdate"]()
        }
    },
    moveItem: function (_, $) {
        if (!_ || !mini.isNumber($)) return;
        if ($ < 0) $ = 0;
        if ($ > this.data.length) $ = this.data.length;
        this.data.remove(_);
        this.data.insert($, _);
        this["doUpdate"]()
    },
    Y2v: function (_, $, C) {
        var A = C ? _[C.field] : this["getItemText"](_),
		D = {
		    sender: this,
		    index: $,
		    rowIndex: $,
		    record: _,
		    item: _,
		    column: C,
		    field: C ? C.field : null,
		    value: A,
		    cellHtml: A,
		    rowCls: null,
		    cellCls: C ? (C.cellCls || "") : "",
		    rowStyle: null,
		    cellStyle: C ? (C.cellStyle || "") : ""
		};
        if (C) {
            if (C.dateFormat) if (mini.isDate(D.value)) D.cellHtml = mini.formatDate(A, C.dateFormat);
            else D.cellHtml = A;
            var B = C.renderer;
            if (B) {
                fn = typeof B == "function" ? B : window[B];
                if (fn) D.cellHtml = fn["call"](C, D)
            }
        }
        this.fire("drawcell", D);
        if (D.cellHtml === null || D.cellHtml === undefined || D.cellHtml === "") D.cellHtml = "&nbsp;";
        return D
    },
    AXlX: function ($) {
        this.VEE.scrollLeft = this.KyT0.scrollLeft
    },
    YY: function (C) {
        var A = this.uid + "$ck$all";
        if (C.target.id == A) {
            var _ = document.getElementById(A);
            if (_) {
                var B = _.checked,
				$ = this.getValue();
                if (B) this.selectAll();
                else this["deselectAll"]();
                this.NAF();
                if ($ != this.getValue()) {
                    this.QWeD();
                    this.fire("itemclick", {
                        htmlEvent: C
                    })
                }
            }
            return
        }
        this.ZP(C, "Click")
    },
    getAttrs: function (_) {
        var E = mini.ListBox["superclass"]["getAttrs"]["call"](this, _);
        mini["_ParseBool"](_, E, ["showCheckBox", "showAllCheckBox", "showNullItem"]);
        if (_.nodeName.toLowerCase() != "select") {
            var C = mini["getChildNodes"](_);
            for (var $ = 0, D = C.length; $ < D; $++) {
                var B = C[$],
				A = jQuery(B).attr("property");
                if (!A) continue;
                A = A.toLowerCase();
                if (A == "columns") E.columns = mini._ParseColumns(B);
                else if (A == "data") E.data = B.innerHTML
            }
        }
        return E
    }
});
HbyG(mini.ListBox, "listbox");
mini.CheckBoxList = function () {
    mini.CheckBoxList["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.CheckBoxList, mini.ListControl, {
    formField: true,
    multiSelect: true,
    repeatItems: 0,
    repeatLayout: "none",
    repeatDirection: "horizontal",
    YUz: "mini-checkboxlist-item",
    Q13: "mini-checkboxlist-item-hover",
    _Cm: "mini-checkboxlist-item-selected",
    W5: "mini-checkboxlist-table",
    QT2U: "mini-checkboxlist-td",
    ICEG: "checkbox",
    uiCls: "mini-checkboxlist",
    _create: function () {
        var $ = this.el = document.createElement("div");
        this.el.className = this.uiCls;
        this.el.innerHTML = "<div class=\"mini-list-inner\"></div><div class=\"mini-errorIcon\"></div><input type=\"hidden\" />";
        this.N$i = this.el.firstChild;
        this.Izde = this.el.lastChild;
        this.PMX = this.el.childNodes[1]
    },
    NfMT: function () {
        var B = [];
        if (this.repeatItems > 0) {
            if (this.repeatDirection == "horizontal") {
                var D = [];
                for (var C = 0, E = this.data.length; C < E; C++) {
                    var A = this.data[C];
                    if (D.length == this.repeatItems) {
                        B.push(D);
                        D = []
                    }
                    D.push(A)
                }
                B.push(D)
            } else {
                var _ = this.repeatItems > this.data.length ? this.data.length : this.repeatItems;
                for (C = 0, E = _; C < E; C++) B.push([]);
                for (C = 0, E = this.data.length; C < E; C++) {
                    var A = this.data[C],
					$ = C % this.repeatItems;
                    B[$].push(A)
                }
            }
        } else B = [this.data.clone()];
        return B
    },
    doUpdate: function () {
        var D = this.data,
		G = "";
        for (var A = 0, F = D.length; A < F; A++) {
            var _ = D[A];
            _._i = A
        }
        if (this.repeatLayout == "flow") {
            var $ = this.NfMT();
            for (A = 0, F = $.length; A < F; A++) {
                var C = $[A];
                for (var E = 0, B = C.length; E < B; E++) {
                    _ = C[E];
                    G += this.LGRj(_, _._i)
                }
                if (A != F - 1) G += "<br/>"
            }
        } else if (this.repeatLayout == "table") {
            $ = this.NfMT();
            G += "<table class=\"" + this.W5 + "\" cellpadding=\"0\" cellspacing=\"1\">";
            for (A = 0, F = $.length; A < F; A++) {
                C = $[A];
                G += "<tr>";
                for (E = 0, B = C.length; E < B; E++) {
                    _ = C[E];
                    G += "<td class=\"" + this.QT2U + "\">";
                    G += this.LGRj(_, _._i);
                    G += "</td>"
                }
                G += "</tr>"
            }
            G += "</table>"
        } else for (A = 0, F = D.length; A < F; A++) {
            _ = D[A];
            G += this.LGRj(_, A)
        }
        this.N$i.innerHTML = G;
        for (A = 0, F = D.length; A < F; A++) {
            _ = D[A];
            delete _._i
        }
    },
    LGRj: function (_, $) {
        var F = this.JpZ(_, $),
		E = this.PNIi($),
		A = this.HDA($),
		C = this["getItemValue"](_),
		B = "",
		D = "<div id=\"" + E + "\" index=\"" + $ + "\" class=\"" + this.YUz + " ";
        if (_.enabled === false) {
            D += " mini-disabled ";
            B = "disabled"
        }
        D += F.itemCls + "\" style=\"" + F.itemStyle + "\"><input " + B + " value=\"" + C + "\" id=\"" + A + "\" type=\"" + this.ICEG + "\" onclick=\"return false;\"/><label for=\"" + A + "\" onclick=\"return false;\">";
        D += F.itemHtml + "</label></div>";
        return D
    },
    JpZ: function (_, $) {
        var A = this["getItemText"](_),
		B = {
		    index: $,
		    item: _,
		    itemHtml: A,
		    itemCls: "",
		    itemStyle: ""
		};
        this.fire("drawitem", B);
        if (B.itemHtml === null || B.itemHtml === undefined) B.itemHtml = "";
        return B
    },
    setRepeatItems: function ($) {
        $ = parseInt($);
        if (isNaN($)) $ = 0;
        if (this.repeatItems != $) {
            this.repeatItems = $;
            this["doUpdate"]()
        }
    },
    getRepeatItems: function () {
        return this.repeatItems
    },
    setRepeatLayout: function ($) {
        if ($ != "flow" && $ != "table") $ = "none";
        if (this.repeatLayout != $) {
            this.repeatLayout = $;
            this["doUpdate"]()
        }
    },
    getRepeatLayout: function () {
        return this.repeatLayout
    },
    setRepeatDirection: function ($) {
        if ($ != "vertical") $ = "horizontal";
        if (this.repeatDirection != $) {
            this.repeatDirection = $;
            this["doUpdate"]()
        }
    },
    getRepeatDirection: function () {
        return this.repeatDirection
    },
    getAttrs: function (_) {
        var D = mini.CheckBoxList["superclass"]["getAttrs"]["call"](this, _),
		C = jQuery(_),
		$ = parseInt(C.attr("repeatItems"));
        if (!isNaN($)) D.repeatItems = $;
        var B = C.attr("repeatLayout");
        if (B) D.repeatLayout = B;
        var A = C.attr("repeatDirection");
        if (A) D.repeatDirection = A;
        return D
    }
});
HbyG(mini.CheckBoxList, "checkboxlist");
mini.RadioButtonList = function () {
    mini.RadioButtonList["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.RadioButtonList, mini.CheckBoxList, {
    multiSelect: false,
    YUz: "mini-radiobuttonlist-item",
    Q13: "mini-radiobuttonlist-item-hover",
    _Cm: "mini-radiobuttonlist-item-selected",
    W5: "mini-radiobuttonlist-table",
    QT2U: "mini-radiobuttonlist-td",
    ICEG: "radio",
    uiCls: "mini-radiobuttonlist"
});
HbyG(mini.RadioButtonList, "radiobuttonlist");
mini.TreeSelect = function () {
    this.data = [];
    mini.TreeSelect["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.TreeSelect, mini.PopupEdit, {
    text: "",
    value: "",
    valueField: "id",
    textField: "text",
    nodesField: "children",
    delimiter: ",",
    multiSelect: false,
    data: [],
    url: "",
    allowInput: false,
    showTreeIcon: false,
    showTreeLines: true,
    resultAsTree: false,
    parentField: "pid",
    checkRecursive: false,
    showFolderCheckBox: false,
    popupWidth: 200,
    popupMaxHeight: 250,
    popupMinWidth: 100,
    set: function (B) {
        if (typeof B == "string") return this;
        var $ = B.value;
        delete B.value;
        var _ = B.text;
        delete B.text;
        var C = B.url;
        delete B.url;
        var A = B.data;
        delete B.data;
        mini.TreeSelect["superclass"].set["call"](this, B);
        if (!mini.isNull(A)) this["setData"](A);
        if (!mini.isNull(C)) this.setUrl(C);
        if (!mini.isNull($)) this["setValue"]($);
        if (!mini.isNull(_)) this["setText"](_);
        return this
    },
    uiCls: "mini-treeselect",
    PLWQ: function () {
        mini.TreeSelect["superclass"].PLWQ["call"](this);
        this.tree = new mini.Tree();
        this.tree.setShowTreeIcon(true);
        this.tree.setStyle("border:0;width:100%;height:100%;");
        this.tree.setResultAsTree(this["resultAsTree"]);
        this.tree["render"](this.popup._contentEl);
        this.tree.on("nodeclick", this.Gs38, this);
        this.tree.on("nodecheck", this.Tq, this);
        this.tree.on("expand", this.TQA, this);
        this.tree.on("collapse", this.PKU, this);
        this.tree.on("beforenodecheck", this.Uln, this);
        this.tree.on("beforenodeselect", this.RWzX, this);
        this.tree.allowAnim = false
    },
    Uln: function ($) {
        $.tree = $.sender;
        this.fire("beforenodecheck", $)
    },
    RWzX: function ($) {
        $.tree = $.sender;
        this.fire("beforenodeselect", $)
    },
    TQA: function ($) {
        this["showPopup"]()
    },
    PKU: function ($) {
        this["showPopup"]()
    },
    showPopup: function () {
        this.tree["setHeight"]("auto");
        var $ = this.popup.el.style.height;
        if ($ == "" || $ == "auto") this.tree["setHeight"]("auto");
        else this.tree["setHeight"]("100%");
        mini.TreeSelect["superclass"]["showPopup"]["call"](this);
        this.tree["setValue"](this.value)
    },
    LNH: function ($) {
        this.tree.clearFilter();
        this.fire("hidepopup")
    },
    getItem: function ($) {
        return typeof $ == "object" ? $ : this.data[$]
    },
    indexOf: function ($) {
        return this.data.indexOf($)
    },
    getAt: function ($) {
        return this.data[$]
    },
    load: function ($) {
        this.tree.load($)
    },
    setData: function ($) {
        this.tree["setData"]($);
        this.data = this.tree.data
    },
    getData: function () {
        return this.data
    },
    setUrl: function ($) {
        this.getPopup();
        this.tree.setUrl($);
        this.url = this.tree.url
    },
    getUrl: function () {
        return this.url
    },
    setTextField: function ($) {
        if (this.tree) this.tree.setTextField($);
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    setNodesField: function ($) {
        if (this.tree) this.tree.setNodesField($);
        this.nodesField = $
    },
    getNodesField: function () {
        return this.nodesField
    },
    setValue: function ($) {
        if (this.value != $) {
            var _ = this.tree.B0$Z($);
            this.value = $;
            this.Izde.value = $;
            if (_[1]) this.VXt.value = _[1];
            this.N_MV()
        }
    },
    setMultiSelect: function ($) {
        if (this["multiSelect"] != $) {
            this["multiSelect"] = $;
            this.tree.setShowCheckBox($);
            this.tree.setAllowSelect(!$)
        }
    },
    getMultiSelect: function () {
        return this["multiSelect"]
    },
    Gs38: function (B) {
        if (this["multiSelect"]) return;
        var _ = this.tree.getSelectedNode(),
		A = this.tree["getItemValue"](_),
		$ = this.getValue();
        this["setValue"](A);
        if ($ != this.getValue()) this.QWeD();
        this["hidePopup"]()
    },
    Tq: function (A) {
        if (!this["multiSelect"]) return;
        var _ = this.tree.getValue(),
		$ = this.getValue();
        this["setValue"](_);
        if ($ != this.getValue()) this.QWeD()
    },
    $Qvl: function (_) {
        this.fire("keydown", {
            htmlEvent: _
        });
        if (_.keyCode == 8 && (this["isReadOnly"]() || this.allowInput == false)) return false;
        if (_.keyCode == 9) {
            this["hidePopup"]();
            return
        }
        switch (_.keyCode) {
            case 27:
                if (this.isShowPopup()) _.stopPropagation();
                this["hidePopup"]();
                break;
            case 13:
                break;
            case 37:
                break;
            case 38:
                _.preventDefault();
                break;
            case 39:
                break;
            case 40:
                _.preventDefault();
                this["showPopup"]();
                break;
            default:
                var $ = this;
                setTimeout(function () {
                    $.$lun()
                },
			10);
                break
        }
    },
    $lun: function () {
        var _ = this["textField"],
		$ = this.VXt.value.toLowerCase();
        this.tree.filter(function (B) {
            var A = String(B[_] ? B[_] : "").toLowerCase();
            if (A.indexOf($) != -1) return true;
            else return false
        });
        this.tree.expandAll();
        this["showPopup"]()
    },
    setCheckRecursive: function ($) {
        this["checkRecursive"] = $;
        if (this.tree) this.tree.setCheckRecursive($)
    },
    getCheckRecursive: function () {
        return this["checkRecursive"]
    },
    setResultAsTree: function ($) {
        this["resultAsTree"] = $;
        if (this.tree) this.tree.setResultAsTree($)
    },
    getResultAsTree: function () {
        return this["resultAsTree"]
    },
    setParentField: function ($) {
        this["parentField"] = $;
        if (this.tree) this.tree.setParentField($)
    },
    getParentField: function () {
        return this["parentField"]
    },
    setValueField: function ($) {
        if (this.tree) this.tree.setIdField($);
        this["valueField"] = $
    },
    getValueField: function () {
        return this["valueField"]
    },
    setShowTreeIcon: function ($) {
        this["showTreeIcon"] = $;
        if (this.tree) this.tree.setShowTreeIcon($)
    },
    getShowTreeIcon: function () {
        return this["showTreeIcon"]
    },
    setShowTreeLines: function ($) {
        this["showTreeLines"] = $;
        if (this.tree) this.tree.setShowTreeLines($)
    },
    getShowTreeLines: function () {
        return this["showTreeLines"]
    },
    setShowFolderCheckBox: function ($) {
        this["showFolderCheckBox"] = $;
        if (this.tree) this.tree.setShowFolderCheckBox($)
    },
    getShowFolderCheckBox: function () {
        return this["showFolderCheckBox"]
    },
    getAttrs: function ($) {
        var _ = mini.ComboBox["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["url", "data", "textField", "valueField", "nodesField", "parentField", "onbeforenodecheck", "onbeforenodeselect"]);
        mini["_ParseBool"]($, _, ["multiSelect", "resultAsTree", "checkRecursive", "showTreeIcon", "showTreeLines", "showFolderCheckBox"]);
        return _
    }
});
HbyG(mini.TreeSelect, "TreeSelect");
mini.Spinner = function () {
    mini.Spinner["superclass"]["constructor"]["call"](this);
    this["setValue"](this["minValue"])
};
Pv_r(mini.Spinner, mini.ButtonEdit, {
    value: 0,
    minValue: 0,
    maxValue: 100,
    increment: 1,
    decimalPlaces: 0,
    set: function (_) {
        if (typeof _ == "string") return this;
        var $ = _.value;
        delete _.value;
        mini.Spinner["superclass"].set["call"](this, _);
        if (!mini.isNull($)) this["setValue"]($);
        return this
    },
    uiCls: "mini-spinner",
    LjKHtml: function () {
        var $ = "onmouseover=\"RE(this,'" + this.Ifmt + "');\" " + "onmouseout=\"WeL(this,'" + this.Ifmt + "');\"";
        return "<span class=\"mini-buttonedit-button\" " + $ + "><span class=\"mini-buttonedit-up\"><span></span></span><span class=\"mini-buttonedit-down\"><span></span></span></span>"
    },
    _initEvents: function () {
        mini.Spinner["superclass"]["_initEvents"]["call"](this);
        KJ_T(function () {
            this.on("buttonmousedown", this.BtCJ, this);
            VNV(this.el, "mousewheel", this.Bys, this)
        },
		this)
    },
    H$cJ: function () {
        if (this["minValue"] > this["maxValue"]) this["maxValue"] = this["minValue"] + 100;
        if (this.value < this["minValue"]) this["setValue"](this["minValue"]);
        if (this.value > this["maxValue"]) this["setValue"](this["maxValue"])
    },
    setValue: function ($) {
        $ = parseFloat($);
        if (isNaN($)) $ = this["minValue"];
        $ = parseFloat($.toFixed(this["decimalPlaces"]));
        if (this.value != $) {
            this.value = $;
            this.H$cJ();
            this.VXt.value = this.Izde.value = this.getFormValue()
        } else this.VXt.value = this.getFormValue()
    },
    setMaxValue: function ($) {
        $ = parseFloat($);
        if (isNaN($)) return;
        $ = parseFloat($.toFixed(this["decimalPlaces"]));
        if (this["maxValue"] != $) {
            this["maxValue"] = $;
            this.H$cJ()
        }
    },
    getMaxValue: function ($) {
        return this["maxValue"]
    },
    setMinValue: function ($) {
        $ = parseFloat($);
        if (isNaN($)) return;
        $ = parseFloat($.toFixed(this["decimalPlaces"]));
        if (this["minValue"] != $) {
            this["minValue"] = $;
            this.H$cJ()
        }
    },
    getMinValue: function ($) {
        return this["minValue"]
    },
    setIncrement: function ($) {
        $ = parseFloat($);
        if (isNaN($)) return;
        if (this["increment"] != $) this["increment"] = $
    },
    getIncrement: function ($) {
        return this["increment"]
    },
    setDecimalPlaces: function ($) {
        $ = parseInt($);
        if (isNaN($) || $ < 0) return;
        this["decimalPlaces"] = $
    },
    getDecimalPlaces: function ($) {
        return this["decimalPlaces"]
    },
    MB_: null,
    Kev: function (D, B, C) {
        this.Bbe();
        this["setValue"](this.value + D);
        var A = this,
		_ = C,
		$ = new Date();
        this.MB_ = setInterval(function () {
            A["setValue"](A.value + D);
            A.QWeD();
            C--;
            if (C == 0 && B > 50) A.Kev(D, B - 100, _ + 3);
            var E = new Date();
            if (E - $ > 500) A.Bbe();
            $ = E
        },
		B);
        VNV(document, "mouseup", this.D3o, this)
    },
    Bbe: function () {
        clearInterval(this.MB_);
        this.MB_ = null
    },
    BtCJ: function ($) {
        this._DownValue = this.getFormValue();
        if ($.spinType == "up") this.Kev(this.increment, 230, 2);
        else this.Kev(-this.increment, 230, 2)
    },
    $Qvl: function (_) {
        mini.Spinner["superclass"].$Qvl["call"](this, _);
        var $ = mini.Keyboard;
        switch (_.keyCode) {
            case $.Top:
                this["setValue"](this.value + this["increment"]);
                this.QWeD();
                break;
            case $.Bottom:
                this["setValue"](this.value - this["increment"]);
                this.QWeD();
                break
        }
    },
    Bys: function (A) {
        if (this["isReadOnly"]()) return;
        var $ = A.wheelDelta;
        if (mini.isNull($)) $ = -A.detail * 24;
        var _ = this["increment"];
        if ($ < 0) _ = -_;
        this["setValue"](this.value + _);
        this.QWeD();
        return false
    },
    D3o: function ($) {
        this.Bbe();
        BP(document, "mouseup", this.D3o, this);
        if (this._DownValue != this.getFormValue()) this.QWeD()
    },
    DvF: function (A) {
        var _ = this.getValue(),
		$ = parseFloat(this.VXt.value);
        this["setValue"]($);
        if (_ != this.getValue()) this.QWeD()
    },
    getAttrs: function ($) {
        var _ = mini.Spinner["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["minValue", "maxValue", "increment", "decimalPlaces"]);
        return _
    }
});
HbyG(mini.Spinner, "spinner");
mini.TimeSpinner = function () {
    mini.TimeSpinner["superclass"]["constructor"]["call"](this);
    this["setValue"]("00:00:00")
};
Pv_r(mini.TimeSpinner, mini.ButtonEdit, {
    value: null,
    format: "H:mm:ss",
    uiCls: "mini-timespinner",
    LjKHtml: function () {
        var $ = "onmouseover=\"RE(this,'" + this.Ifmt + "');\" " + "onmouseout=\"WeL(this,'" + this.Ifmt + "');\"";
        return "<span class=\"mini-buttonedit-button\" " + $ + "><span class=\"mini-buttonedit-up\"><span></span></span><span class=\"mini-buttonedit-down\"><span></span></span></span>"
    },
    _initEvents: function () {
        mini.TimeSpinner["superclass"]["_initEvents"]["call"](this);
        KJ_T(function () {
            this.on("buttonmousedown", this.BtCJ, this);
            VNV(this.el, "mousewheel", this.Bys, this);
            VNV(this.VXt, "keydown", this.M$, this)
        },
		this)
    },
    setFormat: function ($) {
        if (typeof $ != "string") return;
        var _ = ["H:mm:ss", "HH:mm:ss", "H:mm", "HH:mm", "H", "HH", "mm:ss"];
        if (_.indexOf($) == -1) return;
        if (this.format != $) {
            this.format = $;
            this.VXt.value = this.getFormattedValue()
        }
    },
    getFormat: function () {
        return this.format
    },
    setValue: function ($) {
        $ = mini.parseTime($, this.format);
        if (!$) $ = mini.parseTime("00:00:00", this.format);
        if (mini.isDate($)) $ = new Date($.getTime());
        if (mini.formatDate(this.value, "H:mm:ss") != mini.formatDate($, "H:mm:ss")) {
            this.value = $;
            this.VXt.value = this.getFormattedValue();
            this.Izde.value = this.getFormValue()
        }
    },
    getValue: function () {
        return this.value == null ? null : new Date(this.value.getTime())
    },
    getFormValue: function () {
        if (!this.value) return "";
        return mini.formatDate(this.value, "H:mm:ss")
    },
    getFormattedValue: function () {
        if (!this.value) return "";
        return mini.formatDate(this.value, this.format)
    },
    JA: function (D, C) {
        var $ = this.getValue();
        if ($) switch (C) {
            case "hours":
                var A = $.getHours() + D;
                if (A > 23) A = 23;
                if (A < 0) A = 0;
                $.setHours(A);
                break;
            case "minutes":
                var B = $.getMinutes() + D;
                if (B > 59) B = 59;
                if (B < 0) B = 0;
                $.setMinutes(B);
                break;
            case "seconds":
                var _ = $.getSeconds() + D;
                if (_ > 59) _ = 59;
                if (_ < 0) _ = 0;
                $.setSeconds(_);
                break
        } else $ = "00:00:00";
        this["setValue"]($)
    },
    MB_: null,
    Kev: function (D, B, C) {
        this.Bbe();
        this.JA(D, this.Mss);
        var A = this,
		_ = C,
		$ = new Date();
        this.MB_ = setInterval(function () {
            A.JA(D, A.Mss);
            C--;
            if (C == 0 && B > 50) A.Kev(D, B - 100, _ + 3);
            var E = new Date();
            if (E - $ > 500) A.Bbe();
            $ = E
        },
		B);
        VNV(document, "mouseup", this.D3o, this)
    },
    Bbe: function () {
        clearInterval(this.MB_);
        this.MB_ = null
    },
    BtCJ: function ($) {
        this._DownValue = this.getFormValue();
        this.Mss = "hours";
        if ($.spinType == "up") this.Kev(1, 230, 2);
        else this.Kev(-1, 230, 2)
    },
    D3o: function ($) {
        this.Bbe();
        BP(document, "mouseup", this.D3o, this);
        if (this._DownValue != this.getFormValue()) this.QWeD()
    },
    DvF: function (_) {
        var $ = this.getFormValue();
        this["setValue"](this.VXt.value);
        if ($ != this.getFormValue()) this.QWeD()
    },
    getAttrs: function ($) {
        var _ = mini.TimeSpinner["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["format"]);
        return _
    }
});
HbyG(mini.TimeSpinner, "timespinner");
mini.HtmlFile = function () {
    mini.HtmlFile["superclass"]["constructor"]["call"](this);
    this.on("validation", this.FW6, this)
};
Pv_r(mini.HtmlFile, mini.ButtonEdit, {
    width: 180,
    buttonText: "\u6d4f\u89c8...",
    _buttonWidth: 56,
    limitType: "",
    limitTypeErrorText: "\u4e0a\u4f20\u6587\u4ef6\u683c\u5f0f\u4e3a\uff1a",
    allowInput: false,
    readOnly: true,
    Qc8r: 0,
    uiCls: "mini-htmlfile",
    _create: function () {
        mini.HtmlFile["superclass"]["_create"]["call"](this);
        this.I2 = mini.append(this.el, "<input type=\"file\" hideFocus class=\"mini-htmlfile-file\" name=\"" + this.name + "\" ContentEditable=false/>");
        VNV(this.el, "mousemove", this.A$Z, this);
        VNV(this.I2, "change", this.CH8s, this)
    },
    LjKHtml: function () {
        var $ = "onmouseover=\"RE(this,'" + this.Ifmt + "');\" " + "onmouseout=\"WeL(this,'" + this.Ifmt + "');\"";
        return "<span class=\"mini-buttonedit-button\" " + $ + ">" + this.buttonText + "</span>"
    },
    CH8s: function ($) {
        this.value = this.VXt.value = this.I2.value;
        this.QWeD()
    },
    A$Z: function (B) {
        var A = B.pageX,
		_ = B.pageY,
		$ = EcG(this.el);
        A = (A - $.x - 5);
        _ = (_ - $.y - 5);
        if (this.enabled == false) {
            A = -20;
            _ = -20
        }
        this.I2.style.left = A + "px";
        this.I2.style.top = _ + "px"
    },
    FW6: function (B) {
        var A = B.value.split("."),
		$ = "*." + A[A.length - 1],
		_ = this.limitType.split(";");
        if (_.length > 0 && _.indexOf($) == -1) {
            B.errorText = this.limitTypeErrorText + this.limitType;
            B.isValid = false
        }
    },
    setName: function ($) {
        this.name = $;
        mini.setAttr(this.I2, "name", this.name)
    },
    getValue: function () {
        return this.VXt.value
    },
    setButtonText: function ($) {
        this.buttonText = $
    },
    getButtonText: function () {
        return this.buttonText
    },
    setLimitType: function ($) {
        this.limitType = $
    },
    getLimitType: function () {
        return this.limitType
    },
    getAttrs: function ($) {
        var _ = mini.HtmlFile["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["limitType", "buttonText", "limitTypeErrorText"]);
        return _
    }
});
HbyG(mini.HtmlFile, "htmlfile");
mini.FileUpload = function ($) {
    mini.FileUpload["superclass"]["constructor"]["call"](this, $);
    this.on("validation", this.FW6, this)
};
Pv_r(mini.FileUpload, mini.ButtonEdit, {
    width: 180,
    buttonText: "\u6d4f\u89c8...",
    _buttonWidth: 56,
    limitTypeErrorText: "\u4e0a\u4f20\u6587\u4ef6\u683c\u5f0f\u4e3a\uff1a",
    readOnly: true,
    Qc8r: 0,
    limitSize: "",
    limitType: "",
    typesDescription: "\u4e0a\u4f20\u6587\u4ef6\u683c\u5f0f",
    uploadLimit: 0,
    queueLimit: "",
    flashUrl: "",
    uploadUrl: "",
    uploadOnSelect: false,
    uiCls: "mini-fileupload",
    _create: function () {
        mini.FileUpload["superclass"]["_create"]["call"](this);
        RE(this.el, "mini-htmlfile");
        this.I2 = mini.append(this.el, "<span></span>");
        this.uploadEl = this.I2;
        VNV(this.el, "mousemove", this.A$Z, this)
    },
    LjKHtml: function () {
        var $ = "onmouseover=\"RE(this,'" + this.Ifmt + "');\" " + "onmouseout=\"WeL(this,'" + this.Ifmt + "');\"";
        return "<span class=\"mini-buttonedit-button\" " + $ + ">" + this.buttonText + "</span>"
    },
    destroy: function ($) {
        if (this.N$i) {
            mini["clearEvent"](this.N$i);
            this.N$i = null
        }
        mini.FileUpload["superclass"]["destroy"]["call"](this, $)
    },
    A$Z: function (A) {
        var $ = this;
        if (this.enabled == false) return;
        if (!this.swfUpload) {
            var B = new SWFUpload({
                file_post_name: this.name,
                upload_url: $.uploadUrl,
                flash_url: $.flashUrl,
                file_size_limit: $.limitSize,
                file_types: $.limitType,
                file_types_description: $.typesDescription,
                file_upload_limit: parseInt($.uploadLimit),
                file_queue_limit: $.queueLimit,
                file_queued_handler: mini.createDelegate(this.__on_file_queued, this),
                upload_error_handler: mini.createDelegate(this.__on_upload_error, this),
                upload_success_handler: mini.createDelegate(this.__on_upload_success, this),
                upload_complete_handler: mini.createDelegate(this.__on_upload_complete, this),
                button_placeholder: $.uploadEl,
                button_width: 1000,
                button_height: 20,
                button_window_mode: "transparent",
                debug: false
            });
            B.flashReady();
            this.swfUpload = B;
            var _ = this.swfUpload.movieElement;
            _.style.zIndex = 1000;
            _.style.position = "absolute";
            _.style.left = "0px";
            _.style.top = "0px";
            _.style.width = "100%";
            _.style.height = "20px"
        }
    },
    setLimitSize: function ($) {
        this.limitSize = $
    },
    setLimitType: function ($) {
        this.limitType = $
    },
    setTypesDescription: function ($) {
        this.typesDescription = $
    },
    setUploadLimit: function ($) {
        this.uploadLimit = $
    },
    setQueueLimit: function ($) {
        this.queueLimit = $
    },
    setFlashUrl: function ($) {
        this.flashUrl = $
    },
    setUploadUrl: function ($) {
        this.uploadUrl = $
    },
    setName: function ($) {
        this.name = $
    },
    startUpload: function ($) {
        if (this.swfUpload) this.swfUpload.startUpload()
    },
    __on_file_queued: function ($) {
        if (this.uploadOnSelect) this.swfUpload.startUpload();
        this["setText"]($.name)
    },
    __on_upload_success: function (_, $) {
        var A = {
            file: _,
            serverData: $
        };
        this.fire("uploadsuccess", A)
    },
    __on_upload_error: function ($) {
        var _ = {
            file: $
        };
        this.fire("uploaderror", _)
    },
    __on_upload_complete: function ($) {
        this.fire("uploadcomplete", $)
    },
    __fileError: function () { },
    getAttrs: function ($) {
        var _ = mini.FileUpload["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["limitType", "limitSize", "flashUrl", "uploadUrl", "uploadLimit", "onuploadsuccess", "onuploaderror", "onuploadcomplete"]);
        mini["_ParseBool"]($, _, ["uploadOnSelect"]);
        return _
    }
});
HbyG(mini.FileUpload, "fileupload");
mini.Lookup = function () {
    this.data = [];
    mini.Lookup["superclass"]["constructor"]["call"](this);
    VNV(this.VXt, "mouseup", this.ZXp, this)
};
Pv_r(mini.Lookup, mini.PopupEdit, {
    allowInput: true,
    valueField: "id",
    textField: "text",
    delimiter: ",",
    multiSelect: false,
    data: [],
    grid: null,
    uiCls: "mini-lookup",
    destroy: function ($) {
        if (this.grid) {
            this.grid.un("selectionchanged", this.VV8Z, this);
            this.grid.un("load", this.E3, this);
            this.grid = null
        }
        mini.Lookup["superclass"]["destroy"]["call"](this, $)
    },
    setMultiSelect: function ($) {
        this["multiSelect"] = $;
        if (this.grid) this.grid.setMultiSelect($)
    },
    setGrid: function ($) {
        if (typeof $ == "string") {
            mini.parse($);
            $ = mini.get($)
        }
        this.grid = mini.getAndCreate($);
        if (this.grid) {
            this.grid.setMultiSelect(this["multiSelect"]);
            this.grid.setCheckSelectOnLoad(false);
            this.grid.on("selectionchanged", this.VV8Z, this);
            this.grid.on("load", this.E3, this)
        }
    },
    getGrid: function () {
        return this.grid
    },
    setValueField: function ($) {
        this["valueField"] = $
    },
    getValueField: function () {
        return this["valueField"]
    },
    setTextField: function ($) {
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    getItemValue: function ($) {
        return String($[this.valueField])
    },
    getItemText: function ($) {
        var _ = $[this.textField];
        return mini.isNull(_) ? "" : String(_)
    },
    B0$Z: function (A) {
        if (mini.isNull(A)) A = [];
        var B = [],
		C = [];
        for (var _ = 0, D = A.length; _ < D; _++) {
            var $ = A[_];
            if ($) {
                B.push(this["getItemValue"]($));
                C.push(this["getItemText"]($))
            }
        }
        return [B.join(this.delimiter), C.join(this.delimiter)]
    },
    GDo: function (A) {
        var D = {};
        for (var $ = 0, B = A.length; $ < B; $++) {
            var _ = A[$],
			C = _[this.valueField];
            D[C] = _
        }
        return D
    },
    VV8Z: function (G) {
        var B = this.GDo(this.grid.getData()),
		C = this.GDo(this.grid.getSelecteds()),
		F = this.GDo(this.data);
        if (this["multiSelect"] == false) {
            F = {};
            this.data = []
        }
        var A = {};
        for (var E in F) {
            var $ = F[E];
            if (B[E]) if (C[E]);
            else A[E] = $
        }
        for (var _ = this.data.length - 1; _ >= 0; _--) {
            $ = this.data[_],
			E = $[this.valueField];
            if (A[E]) this.data.removeAt(_)
        }
        for (E in C) {
            $ = C[E];
            if (!F[E]) this.data.push($)
        }
        var D = this.B0$Z(this.data);
        this["setValue"](D[0]);
        this["setText"](D[1]);
        this.QWeD()
    },
    E3: function (H) {
        var C = this.value.split(this.delimiter),
		F = {};
        for (var $ = 0, D = C.length; $ < D; $++) {
            var G = C[$];
            F[G] = 1
        }
        var A = this.grid.getData(),
		B = [];
        for ($ = 0, D = A.length; $ < D; $++) {
            var _ = A[$],
			E = _[this.valueField];
            if (F[E]) B.push(_)
        }
        this.grid["selects"](B)
    },
    doUpdate: function () {
        mini.Lookup["superclass"]["doUpdate"]["call"](this);
        this.VXt["readOnly"] = true;
        this.el.style.cursor = "default"
    },
    $Qvl: function ($) {
        mini.Lookup["superclass"].$Qvl["call"](this, $);
        switch ($.keyCode) {
            case 46:
            case 8:
                break;
            case 37:
                break;
            case 39:
                break
        }
    },
    ZXp: function (C) {
        if (this["isReadOnly"]()) return;
        var _ = mini.getSelectRange(this.VXt),
		A = _[0],
		B = _[1],
		$ = this.Ez(A)
    },
    Ez: function (E) {
        var _ = -1;
        if (this.text == "") return _;
        var C = this.text.split(this.delimiter),
		$ = 0;
        for (var A = 0, D = C.length; A < D; A++) {
            var B = C[A];
            if ($ < E && E <= $ + B.length) {
                _ = A;
                break
            }
            $ = $ + B.length + 1
        }
        return _
    },
    getAttrs: function ($) {
        var _ = mini.Lookup["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["grid", "valueField", "textField"]);
        mini["_ParseBool"]($, _, ["multiSelect"]);
        return _
    }
});
HbyG(mini.Lookup, "lookup");
mini.TextBoxList = function () {
    mini.TextBoxList["superclass"]["constructor"]["call"](this);
    this.data = [];
    this["doUpdate"]()
};
Pv_r(mini.TextBoxList, mini.ValidatorBase, {
    formField: true,
    value: "",
    text: "",
    valueField: "id",
    textField: "text",
    url: "",
    delay: 250,
    allowInput: true,
    editIndex: 0,
    FS: "mini-textboxlist-focus",
    Udt: "mini-textboxlist-item-hover",
    MyX: "mini-textboxlist-item-selected",
    I0z: "mini-textboxlist-close-hover",
    textName: "",
    setTextName: function ($) {
        this.textName = $
    },
    getTextName: function () {
        return this.textName
    },
    uiCls: "mini-textboxlist",
    _create: function () {
        var A = "<table class=\"mini-textboxlist\" cellpadding=\"0\" cellspacing=\"0\"><tr ><td class=\"mini-textboxlist-border\"><ul></ul><a href=\"#\"></a><input type=\"hidden\"/></td></tr></table>",
		_ = document.createElement("div");
        _.innerHTML = A;
        this.el = _.firstChild;
        var $ = this.el.getElementsByTagName("td")[0];
        this.ulEl = $.firstChild;
        this.Izde = $.lastChild;
        this.focusEl = $.childNodes[1]
    },
    destroy: function ($) {
        if (this.isShowPopup) this["hidePopup"]();
        BP(document, "mousedown", this.La2, this);
        mini.TextBoxList["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        mini.TextBoxList["superclass"]["_initEvents"]["call"](this);
        VNV(this.el, "mousemove", this.A$Z, this);
        VNV(this.el, "mouseout", this.Gj$, this);
        VNV(this.el, "mousedown", this.UNAE, this);
        VNV(this.el, "click", this.YY, this);
        VNV(this.el, "keydown", this.M$, this);
        VNV(document, "mousedown", this.La2, this)
    },
    La2: function ($) {
        if (this["isReadOnly"]()) return false;
        if (this.isShowPopup) if (!Yma(this.popup.el, $.target)) this["hidePopup"]();
        if (this.FNnW) if (this["within"]($) == false) {
            this["select"](null, false);
            this.showInput(false);
            this["removeCls"](this.FS);
            this.FNnW = false
        }
    },
    errorIconEl: null,
    getErrorIconEl: function () {
        if (!this.PMX) {
            var _ = this.el.rows[0],
			$ = _.insertCell(1);
            $.style.cssText = "width:18px;vertical-align:top;";
            $.innerHTML = "<div class=\"mini-errorIcon\"></div>";
            this.PMX = $.firstChild
        }
        return this.PMX
    },
    Keo: function () {
        if (this.PMX) jQuery(this.PMX.parentNode).remove();
        this.PMX = null
    },
    doLayout: function () {
        if (this.canLayout() == false) return;
        mini.TextBoxList["superclass"]["doLayout"]["call"](this);
        if (this["isReadOnly"]() || this.allowInput == false) this.HtIe["readOnly"] = true;
        else this.HtIe["readOnly"] = false
    },
    doUpdate: function () {
        if (this.VgbV) clearInterval(this.VgbV);
        if (this.HtIe) BP(this.HtIe, "keydown", this.$Qvl, this);
        var G = [],
		F = this.uid;
        for (var A = 0, E = this.data.length; A < E; A++) {
            var _ = this.data[A],
			C = F + "$text$" + A,
			B = _[this.textField];
            if (mini.isNull(B)) B = "";
            G[G.length] = "<li id=\"" + C + "\" class=\"mini-textboxlist-item\">";
            G[G.length] = B;
            G[G.length] = "<span class=\"mini-textboxlist-close\"></span></li>"
        }
        var $ = F + "$input";
        G[G.length] = "<li id=\"" + $ + "\" class=\"mini-textboxlist-inputLi\"><input class=\"mini-textboxlist-input\" type=\"text\" autocomplete=\"off\"></li>";
        this.ulEl.innerHTML = G.join("");
        this.editIndex = this.data.length;
        if (this.editIndex < 0) this.editIndex = 0;
        this.inputLi = this.ulEl.lastChild;
        this.HtIe = this.inputLi.firstChild;
        VNV(this.HtIe, "keydown", this.$Qvl, this);
        var D = this;
        this.HtIe.onkeyup = function () {
            D.X6G()
        };
        D.VgbV = null;
        D.Pz8q = D.HtIe.value;
        this.HtIe.onfocus = function () {
            D.VgbV = setInterval(function () {
                if (D.Pz8q != D.HtIe.value) {
                    D.Ae();
                    D.Pz8q = D.HtIe.value
                }
            },
			10);
            D["addCls"](D.FS);
            D.FNnW = true
        };
        this.HtIe.onblur = function () {
            clearInterval(D.VgbV)
        }
    },
    H37a: function (_) {
        var A = MRj9(_.target, "mini-textboxlist-item");
        if (A) {
            var $ = A.id.split("$"),
			B = $[$.length - 1];
            return this.data[B]
        }
    },
    getItem: function ($) {
        if (typeof $ == "number") return this.data[$];
        if (typeof $ == "object") return $
    },
    getItemEl: function (_) {
        var $ = this.data.indexOf(_),
		A = this.uid + "$text$" + $;
        return document.getElementById(A)
    },
    hoverItem: function ($, A) {
        this.blurItem();
        var _ = this.getItemEl($);
        RE(_, this.Udt);
        if (A && D4ge(A.target, "mini-textboxlist-close")) RE(A.target, this.I0z)
    },
    blurItem: function () {
        var _ = this.data.length;
        for (var A = 0, C = _; A < C; A++) {
            var $ = this.data[A],
			B = this.getItemEl($);
            if (B) {
                WeL(B, this.Udt);
                WeL(B.lastChild, this.I0z)
            }
        }
    },
    showInput: function (A) {
        this["select"](null);
        if (mini.isNumber(A)) this.editIndex = A;
        else this.editIndex = this.data.length;
        if (this.editIndex < 0) this.editIndex = 0;
        if (this.editIndex > this.data.length) this.editIndex = this.data.length;
        var B = this.inputLi;
        B.style.display = "block";
        if (mini.isNumber(A) && A < this.data.length) {
            var _ = this.data[A],
			$ = this.getItemEl(_);
            jQuery($).before(B)
        } else this.ulEl.appendChild(B);
        if (A !== false) setTimeout(function () {
            try {
                B.firstChild.focus();
                mini.selectRange(B.firstChild, 100)
            } catch ($) { }
        },
		10);
        else {
            this.lastInputText = "";
            this.HtIe.value = ""
        }
        return B
    },
    select: function (_) {
        _ = this["getItem"](_);
        if (this.RrP) {
            var $ = this.getItemEl(this.RrP);
            WeL($, this.MyX)
        }
        this.RrP = _;
        if (this.RrP) {
            $ = this.getItemEl(this.RrP);
            RE($, this.MyX)
        }
        var A = this;
        if (this.RrP) {
            this.focusEl.focus();
            var B = this;
            setTimeout(function () {
                try {
                    B.focusEl.focus()
                } catch ($) { }
            },
			50)
        }
        if (this.RrP) {
            A["addCls"](A.FS);
            A.FNnW = true
        }
    },
    K4l: function () {
        var _ = this.QM["getSelected"](),
		$ = this.editIndex;
        if (_) {
            _ = mini.clone(_);
            this.insertItem($, _)
        }
    },
    insertItem: function (_, $) {
        this.data.insert(_, $);
        var B = this.getText(),
		A = this.getValue();
        this["setValue"](A, false);
        this["setText"](B, false);
        this.Llsz();
        this["doUpdate"]();
        this.showInput(_ + 1);
        this.QWeD()
    },
    removeItem: function (_) {
        if (!_) return;
        var $ = this.getItemEl(_);
        mini["removeNode"]($);
        this.data.remove(_);
        var B = this.getText(),
		A = this.getValue();
        this["setValue"](A, false);
        this["setText"](B, false);
        this.QWeD()
    },
    Llsz: function () {
        var C = (this.text ? this.text : "").split(","),
		B = (this.value ? this.value : "").split(",");
        if (B[0] == "") B = [];
        var _ = B.length;
        this.data.length = _;
        for (var A = 0, D = _; A < D; A++) {
            var $ = this.data[A];
            if (!$) {
                $ = {};
                this.data[A] = $
            }
            $[this.textField] = !mini.isNull(C[A]) ? C[A] : "";
            $[this.valueField] = !mini.isNull(B[A]) ? B[A] : ""
        }
        this.value = this.getValue();
        this.text = this.getText()
    },
    getInputText: function () {
        return this.HtIe ? this.HtIe.value : ""
    },
    getText: function () {
        var C = [];
        for (var _ = 0, A = this.data.length; _ < A; _++) {
            var $ = this.data[_],
			B = $[this.textField];
            if (mini.isNull(B)) B = "";
            B = B.replace(",", "\uff0c");
            C.push(B)
        }
        return C.join(",")
    },
    getValue: function () {
        var B = [];
        for (var _ = 0, A = this.data.length; _ < A; _++) {
            var $ = this.data[_];
            B.push($[this.valueField])
        }
        return B.join(",")
    },
    setName: function ($) {
        if (this.name != $) {
            this.name = $;
            this.Izde.name = $
        }
    },
    setValue: function ($) {
        if (mini.isNull($)) $ = "";
        if (this.value != $) {
            this.value = $;
            this.Izde.value = $;
            this.Llsz();
            this["doUpdate"]()
        }
    },
    setText: function ($) {
        if (mini.isNull($)) $ = "";
        if (this.text !== $) {
            this.text = $;
            this.Llsz();
            this["doUpdate"]()
        }
    },
    setValueField: function ($) {
        this["valueField"] = $
    },
    getValueField: function () {
        return this["valueField"]
    },
    setTextField: function ($) {
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    setAllowInput: function ($) {
        this.allowInput = $;
        this["doLayout"]()
    },
    getAllowInput: function () {
        return this.allowInput
    },
    setUrl: function ($) {
        this.url = $
    },
    getUrl: function () {
        return this.url
    },
    setPopupHeight: function ($) {
        this["popupHeight"] = $
    },
    getPopupHeight: function () {
        return this["popupHeight"]
    },
    setPopupMinHeight: function ($) {
        this["popupMinHeight"] = $
    },
    getPopupMinHeight: function () {
        return this["popupMinHeight"]
    },
    setPopupMaxHeight: function ($) {
        this["popupMaxHeight"] = $
    },
    getPopupMaxHeight: function () {
        return this["popupMaxHeight"]
    },
    X6G: function () {
        if (this["isDisplay"]() == false) return;
        var _ = this.getInputText(),
		B = mini.measureText(this.HtIe, _),
		$ = B.width > 20 ? B.width + 4 : 20,
		A = L_h(this.el, true);
        if ($ > A - 15) $ = A - 15;
        this.HtIe.style.width = $ + "px"
    },
    Ae: function (_) {
        var $ = this;
        setTimeout(function () {
            $.X6G()
        },
		1);
        this["showPopup"]("loading");
        this.Bhps();
        this._loading = true;
        this.delayTimer = setTimeout(function () {
            var _ = $.HtIe.value;
            $.$lun()
        },
		this.delay)
    },
    $lun: function () {
        if (this["isDisplay"]() == false) return;
        var _ = this.getInputText(),
		A = this,
		$ = this.QM.getData(),
		B = {
		    key: _,
		    value: this.getValue(),
		    text: this.getText()
		},
		C = this.url,
		E = typeof C == "function" ? C : window[C];
        if (typeof E == "function") C = E(this);
        if (!C) return;
        var D = {
            url: C,
            async: true,
            data: B,
            type: "GET",
            cache: false,
            dataType: "text",
            cancel: false
        };
        this.fire("beforeload", D);
        if (D.cancel) return;
        mini.copyTo(D, {
            success: function ($) {
                var _ = mini.decode($);
                A.QM["setData"](_);
                A["showPopup"]();
                A.QM.M9(0, true);
                A.fire("load");
                A._loading = false;
                if (A._selectOnLoad) {
                    A.__doSelectValue();
                    A._selectOnLoad = null
                }
            },
            error: function ($, B, _) {
                A["showPopup"]("error")
            }
        });
        A.AJy = jQuery.ajax(D)
    },
    Bhps: function () {
        if (this.delayTimer) {
            clearTimeout(this.delayTimer);
            this.delayTimer = null
        }
        if (this.AJy) this.AJy.abort();
        this._loading = false
    },
    within: function ($) {
        if (Yma(this.el, $.target)) return true;
        if (this["showPopup"] && this.popup && this.popup["within"]($)) return true;
        return false
    },
    popupLoadingText: "<span class='mini-textboxlist-popup-loading'>Loading...</span>",
    popupErrorText: "<span class='mini-textboxlist-popup-error'>Error</span>",
    popupEmptyText: "<span class='mini-textboxlist-popup-noresult'>No Result</span>",
    isShowPopup: false,
    popupHeight: "",
    popupMinHeight: 30,
    popupMaxHeight: 150,
    PLWQ: function () {
        if (!this.popup) {
            this.popup = new mini.ListBox();
            this.popup["addCls"]("mini-textboxlist-popup");
            this.popup.setStyle("position:absolute;left:0;top:0;");
            this.popup["showEmpty"] = true;
            this.popup["setValueField"](this["valueField"]);
            this.popup.setTextField(this["textField"]);
            this.popup["render"](document.body);
            this.popup.on("itemclick",
			function ($) {
			    this["hidePopup"]();
			    this.K4l()
			},
			this)
        }
        this.QM = this.popup;
        return this.popup
    },
    showPopup: function ($) {
        this.isShowPopup = true;
        var _ = this.PLWQ();
        _.el.style.zIndex = mini.getMaxZIndex();
        var B = this.QM;
        B["emptyText"] = this.popupEmptyText;
        if ($ == "loading") {
            B["emptyText"] = this.popupLoadingText;
            this.QM["setData"]([])
        } else if ($ == "error") {
            B["emptyText"] = this.popupLoadingText;
            this.QM["setData"]([])
        }
        this.QM["doUpdate"]();
        var A = this.getBox(),
		D = A.x,
		C = A.y + A.height;
        this.popup.el.style.display = "block";
        mini["setXY"](_.el, -1000, -1000);
        this.popup["setWidth"](A.width);
        this.popup["setHeight"](this["popupHeight"]);
        if (this.popup["getHeight"]() < this["popupMinHeight"]) this.popup["setHeight"](this["popupMinHeight"]);
        if (this.popup["getHeight"]() > this["popupMaxHeight"]) this.popup["setHeight"](this["popupMaxHeight"]);
        mini["setXY"](_.el, D, C)
    },
    hidePopup: function () {
        this.isShowPopup = false;
        if (this.popup) this.popup.el.style.display = "none"
    },
    A$Z: function (_) {
        if (this.enabled == false) return;
        var $ = this.H37a(_);
        if (!$) {
            this.blurItem();
            return
        }
        this.hoverItem($, _)
    },
    Gj$: function ($) {
        this.blurItem()
    },
    YY: function (_) {
        if (this.enabled == false) return;
        var $ = this.H37a(_);
        if (!$) {
            if (MRj9(_.target, "mini-textboxlist-input"));
            else this.showInput();
            return
        }
        this.focusEl.focus();
        this["select"]($);
        if (_ && D4ge(_.target, "mini-textboxlist-close")) this.removeItem($)
    },
    M$: function (B) {
        if (this["isReadOnly"]() || this.allowInput == false) return false;
        var $ = this.data.indexOf(this.RrP),
		_ = this;
        function A() {
            var A = _.data[$];
            _.removeItem(A);
            A = _.data[$];
            if (!A) A = _.data[$ - 1];
            _["select"](A);
            if (!A) _.showInput()
        }
        switch (B.keyCode) {
            case 8:
                B.preventDefault();
                A();
                break;
            case 37:
            case 38:
                this["select"](null);
                this.showInput($);
                break;
            case 39:
            case 40:
                $ += 1;
                this["select"](null);
                this.showInput($);
                break;
            case 46:
                A();
                break
        }
    },
    __doSelectValue: function () {
        var $ = this.QM.getFocusedItem();
        if ($) this.QM["setSelected"]($);
        this.lastInputText = this.text;
        this["hidePopup"]();
        this.K4l()
    },
    $Qvl: function (G) {
        this._selectOnLoad = null;
        if (this["isReadOnly"]() || this.allowInput == false) return false;
        G.stopPropagation();
        if (this["isReadOnly"]() || this.allowInput == false) return;
        var E = mini.getSelectRange(this.HtIe),
		B = E[0],
		D = E[1],
		F = this.HtIe.value.length,
		C = B == D && B == 0,
		A = B == D && D == F;
        if (this["isReadOnly"]() || this.allowInput == false) G.preventDefault();
        if (G.keyCode == 9) {
            this["hidePopup"]();
            return
        }
        if (G.keyCode == 16 || G.keyCode == 17 || G.keyCode == 18) return;
        switch (G.keyCode) {
            case 13:
                if (this.isShowPopup) {
                    G.preventDefault();
                    if (this._loading) {
                        this._selectOnLoad = true;
                        return
                    }
                    this.__doSelectValue()
                }
                break;
            case 27:
                G.preventDefault();
                this["hidePopup"]();
                break;
            case 8:
                if (C) G.preventDefault();
            case 37:
                if (C) if (this.isShowPopup) this["hidePopup"]();
                else if (this.editIndex > 0) {
                    var _ = this.editIndex - 1;
                    if (_ < 0) _ = 0;
                    if (_ >= this.data.length) _ = this.data.length - 1;
                    this.showInput(false);
                    this["select"](_)
                }
                break;
            case 39:
                if (A) if (this.isShowPopup) this["hidePopup"]();
                else if (this.editIndex <= this.data.length - 1) {
                    _ = this.editIndex;
                    this.showInput(false);
                    this["select"](_)
                }
                break;
            case 38:
                G.preventDefault();
                if (this.isShowPopup) {
                    var _ = -1,
				$ = this.QM.getFocusedItem();
                    if ($) _ = this.QM.indexOf($);
                    _--;
                    if (_ < 0) _ = 0;
                    this.QM.M9(_, true)
                }
                break;
            case 40:
                G.preventDefault();
                if (this.isShowPopup) {
                    _ = -1,
				$ = this.QM.getFocusedItem();
                    if ($) _ = this.QM.indexOf($);
                    _++;
                    if (_ < 0) _ = 0;
                    if (_ >= this.QM.getCount()) _ = this.QM.getCount() - 1;
                    this.QM.M9(_, true)
                } else this.Ae(true);
                break;
            default:
                break
        }
    },
    focus: function () {
        try {
            this.HtIe.focus()
        } catch ($) { }
    },
    blur: function () {
        try {
            this.HtIe.blur()
        } catch ($) { }
    },
    getAttrs: function ($) {
        var A = mini.TextBox["superclass"]["getAttrs"]["call"](this, $),
		_ = jQuery($);
        mini["_ParseString"]($, A, ["value", "text", "valueField", "textField", "url", "popupHeight", "textName"]);
        mini["_ParseBool"]($, A, ["allowInput"]);
        mini["_ParseInt"]($, A, ["popupMinHeight", "popupMaxHeight"]);
        return A
    }
});
HbyG(mini.TextBoxList, "textboxlist");
mini.AutoComplete = function () {
    mini.AutoComplete["superclass"]["constructor"]["call"](this);
    var $ = this;
    $.VgbV = null;
    this.VXt.onfocus = function () {
        $.Pz8q = $.VXt.value;
        $.VgbV = setInterval(function () {
            if ($.Pz8q != $.VXt.value) {
                $.F1OV();
                $.Pz8q = $.VXt.value;
                if ($.VXt.value == "" && $.value != "") {
                    $["setValue"]("");
                    $.QWeD()
                }
            }
        },
		10)
    };
    this.VXt.onblur = function () {
        clearInterval($.VgbV);
        if (!$.isShowPopup()) if ($.Pz8q != $.VXt.value) if ($.VXt.value == "" && $.value != "") {
            $["setValue"]("");
            $.QWeD()
        }
    };
    this._buttonEl.style.display = "none"
};
Pv_r(mini.AutoComplete, mini.ComboBox, {
    url: "",
    allowInput: true,
    delay: 250,
    _buttonWidth: 0,
    uiCls: "mini-autocomplete",
    setUrl: function ($) {
        this.url = $
    },
    setValue: function ($) {
        if (this.value != $) {
            this.value = $;
            this.Izde.value = this.value
        }
    },
    setText: function ($) {
        if (this.text != $) {
            this.text = $;
            this.Pz8q = $
        }
        this.VXt.value = this.text
    },
    popupLoadingText: "<span class='mini-textboxlist-popup-loading'>Loading...</span>",
    popupErrorText: "<span class='mini-textboxlist-popup-error'>Error</span>",
    popupEmptyText: "<span class='mini-textboxlist-popup-noresult'>No Result</span>",
    showPopup: function ($) {
        var _ = this.getPopup(),
		A = this.QM;
        A["showEmpty"] = true;
        A["emptyText"] = this.popupEmptyText;
        if ($ == "loading") {
            A["emptyText"] = this.popupLoadingText;
            this.QM["setData"]([])
        } else if ($ == "error") {
            A["emptyText"] = this.popupLoadingText;
            this.QM["setData"]([])
        }
        this.QM["doUpdate"]();
        mini.AutoComplete["superclass"]["showPopup"]["call"](this)
    },
    $Qvl: function (C) {
        this.fire("keydown", {
            htmlEvent: C
        });
        if (C.keyCode == 8 && (this["isReadOnly"]() || this.allowInput == false)) return false;
        if (C.keyCode == 9) {
            this["hidePopup"]();
            return
        }
        switch (C.keyCode) {
            case 27:
                if (this.isShowPopup()) C.stopPropagation();
                this["hidePopup"]();
                break;
            case 13:
                if (this.isShowPopup()) {
                    C.preventDefault();
                    C.stopPropagation();
                    var _ = this.QM.getFocusedIndex();
                    if (_ != -1) {
                        var $ = this.QM.getAt(_),
					B = this.QM.B0$Z([$]),
					A = B[0];
                        this["setValue"](A);
                        this["setText"](B[1]);
                        this.QWeD();
                        this["hidePopup"]()
                    }
                } else this.fire("enter");
                break;
            case 37:
                break;
            case 38:
                _ = this.QM.getFocusedIndex();
                if (_ == -1) {
                    _ = 0;
                    if (!this["multiSelect"]) {
                        $ = this.QM.findItems(this.value)[0];
                        if ($) _ = this.QM.indexOf($)
                    }
                }
                if (this.isShowPopup()) if (!this["multiSelect"]) {
                    _ -= 1;
                    if (_ < 0) _ = 0;
                    this.QM.M9(_, true)
                }
                break;
            case 39:
                break;
            case 40:
                _ = this.QM.getFocusedIndex();
                if (this.isShowPopup()) {
                    if (!this["multiSelect"]) {
                        _ += 1;
                        if (_ > this.QM.getCount() - 1) _ = this.QM.getCount() - 1;
                        this.QM.M9(_, true)
                    }
                } else this.F1OV(this.VXt.value);
                break;
            default:
                this.F1OV(this.VXt.value);
                break
        }
    },
    F1OV: function (_) {
        var $ = this;
        if (this._queryTimer) {
            clearTimeout(this._queryTimer);
            this._queryTimer = null
        }
        this._queryTimer = setTimeout(function () {
            var _ = $.VXt.value;
            $.$lun(_)
        },
		this.delay);
        this["showPopup"]("loading")
    },
    $lun: function ($) {
        if (!this.url) return;
        if (this.AJy) this.AJy.abort();
        var _ = this;
        this.AJy = jQuery.ajax({
            url: this.url,
            data: {
                key: $
            },
            async: true,
            cache: false,
            dataType: "text",
            success: function ($) {
                try {
                    var A = mini.decode($)
                } catch (B) {
                    throw new Error("autocomplete json is error")
                }
                _.QM["setData"](A);
                _["showPopup"]();
                _.QM.M9(0, true);
                _.fire("load")
            },
            error: function ($, B, A) {
                _["showPopup"]("error")
            }
        })
    },
    getAttrs: function ($) {
        var A = mini.AutoComplete["superclass"]["getAttrs"]["call"](this, $),
		_ = jQuery($);
        return A
    }
});
HbyG(mini.AutoComplete, "autocomplete");
mini.Form = function ($) {
    this.el = E1R($);
    if (!this.el) throw new Error("form element not null");
    mini.Form["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Form, mini.Component, {
    el: null,
    getFields: function () {
        if (!this.el) return [];
        var $ = mini.findControls(function ($) {
            if (!$.el || $.formField != true) return false;
            if (Yma(this.el, $.el)) return true;
            return false
        },
		this);
        return $
    },
    getFieldsMap: function () {
        var B = this.getFields(),
		A = {};
        for (var $ = 0, C = B.length; $ < C; $++) {
            var _ = B[$];
            if (_.name) A[_.name] = _
        }
        return A
    },
    getField: function ($) {
        if (!this.el) return null;
        return mini.getbyName($, this.el)
    },
    getData: function (B) {
        var A = B ? "getFormValue" : "getValue",
		$ = this.getFields(),
		D = {};
        for (var _ = 0, E = $.length; _ < E; _++) {
            var C = $[_],
			F = C[A];
            if (!F) continue;
            if (C.name) D[C.name] = F["call"](C);
            if (C.textName && C.getText) D[C.textName] = C.getText()
        }
        return D
    },
    setData: function (E, A) {
        if (typeof E != "object") E = {};
        var B = this.getFieldsMap();
        for (var C in B) {
            var _ = B[C];
            if (!_) continue;
            if (_["setValue"]) {
                var D = E[C];
                if (D === undefined && A === false) continue;
                if (D === null) D = "";
                _["setValue"](D)
            }
            if (_["setText"] && _.textName) {
                var $ = E[_.textName] || "";
                _["setText"]($)
            }
        }
    },
    reset: function () {
        var $ = this.getFields();
        for (var _ = 0, B = $.length; _ < B; _++) {
            var A = $[_];
            if (!A["setValue"]) continue;
            A["setValue"](A["defaultValue"])
        }
        this.setIsValid(true)
    },
    clear: function () {
        var $ = this.getFields();
        for (var _ = 0, B = $.length; _ < B; _++) {
            var A = $[_];
            if (!A["setValue"]) continue;
            A["setValue"]("")
        }
        this.setIsValid(true)
    },
    validate: function (C) {
        var $ = this.getFields();
        for (var _ = 0, D = $.length; _ < D; _++) {
            var A = $[_];
            if (!A["validate"]) continue;
            var B = A["validate"]();
            if (B == false && C === false) break
        }
        return this.isValid()
    },
    setIsValid: function (B) {
        var $ = this.getFields();
        for (var _ = 0, C = $.length; _ < C; _++) {
            var A = $[_];
            if (!A.setIsValid) continue;
            A.setIsValid(B)
        }
    },
    isValid: function () {
        var $ = this.getFields();
        for (var _ = 0, B = $.length; _ < B; _++) {
            var A = $[_];
            if (!A.isValid) continue;
            if (A.isValid() == false) return false
        }
        return true
    },
    getErrorTexts: function () {
        var A = [],
		_ = this.getErrors();
        for (var $ = 0, C = _.length; $ < C; $++) {
            var B = _[$];
            A.push(B.errorText)
        }
        return A
    },
    getErrors: function () {
        var A = [],
		$ = this.getFields();
        for (var _ = 0, C = $.length; _ < C; _++) {
            var B = $[_];
            if (!B.isValid) continue;
            if (B.isValid() == false) A.push(B)
        }
        return A
    },
    mask: function ($) {
        if (typeof $ == "string") $ = {
            html: $
        };
        $ = $ || {};
        $.el = this.el;
        if (!$.cls) $.cls = this.Buy;
        mini.mask($)
    },
    unmask: function () {
        mini.unmask(this.el)
    },
    Buy: "mini-mask-loading",
    loadingMsg: "\u6570\u636e\u52a0\u8f7d\u4e2d\uff0c\u8bf7\u7a0d\u540e...",
    loading: function ($) {
        this.mask($ || this.loadingMsg)
    },
    NWj: function ($) {
        this._changed = true
    },
    _changed: false,
    setChanged: function (A) {
        this._changed = A;
        var $ = form.getFields();
        for (var _ = 0, C = $.length; _ < C; _++) {
            var B = $[_];
            B.on("valuechanged", this.NWj, this)
        }
    },
    isChanged: function () {
        return this._changed
    },
    setEnabled: function (A) {
        var $ = form.getFields();
        for (var _ = 0, C = $.length; _ < C; _++) {
            var B = $[_];
            B.setEnabled(A)
        }
    }
});
mini.Fit = function () {
    mini.Fit["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Fit, mini.Control, {
    style: "",
    _clearBorder: false,
    uiCls: "mini-fit",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-fit";
        this.$kJ = this.el
    },
    _initEvents: function () { },
    isFixedSize: function () {
        return false
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        var $ = this.el.parentNode,
		_ = mini["getChildNodes"]($);
        if ($ == document.body) this.el.style.height = "0px";
        var F = Nf($, true);
        for (var E = 0, D = _.length; E < D; E++) {
            var C = _[E];
            if (C == this.el) continue;
            var G = YI(C, "position");
            if (G == "absolute" || G == "fixed") continue;
            var A = Nf(C),
			I = OFY(C);
            F = F - A - I.top - I.bottom
        }
        var H = Ly5(this.el),
		B = M32(this.el),
		I = OFY(this.el);
        F = F - I.top - I.bottom;
        if (jQuery.boxModel) F = F - B.top - B.bottom - H.top - H.bottom;
        if (F < 0) F = 0;
        this.el.style.height = F + "px";
        try {
            _ = mini["getChildNodes"](this.el);
            for (E = 0, D = _.length; E < D; E++) {
                C = _[E];
                mini.layout(C)
            }
        } catch (J) { }
    },
    set_bodyParent: function ($) {
        if (!$) return;
        var _ = this.$kJ,
		A = $;
        while (A.firstChild) {
            try {
                _.appendChild(A.firstChild)
            } catch (B) { }
        }
        this["doLayout"]()
    },
    getAttrs: function ($) {
        var _ = mini.Fit["superclass"]["getAttrs"]["call"](this, $);
        _._bodyParent = $;
        return _
    }
});
HbyG(mini.Fit, "fit");
mini.Panel = function () {
    this.DEv3();
    mini.Panel["superclass"]["constructor"]["call"](this);
    if (this.url) this.setUrl(this.url)
};
Pv_r(mini.Panel, mini.Control, {
    width: 250,
    title: "",
    iconCls: "",
    iconStyle: "",
    url: "",
    refreshOnExpand: false,
    maskOnLoad: true,
    showCollapseButton: false,
    showCloseButton: false,
    closeAction: "display",
    showHeader: true,
    showToolbar: false,
    showFooter: false,
    headerCls: "",
    headerStyle: "",
    bodyCls: "",
    bodyStyle: "",
    footerCls: "",
    footerStyle: "",
    toolbarCls: "",
    toolbarStyle: "",
    set: function (_) {
        if (typeof _ == "string") return this;
        var B = this.SX1V;
        this.SX1V = false;
        var C = _.toolbar;
        delete _.toolbar;
        var $ = _.footer;
        delete _.footer;
        var A = _.url;
        delete _.url;
        mini.Panel["superclass"].set["call"](this, _);
        if (C) this.setToolbar(C);
        if ($) this.setFooter($);
        if (A) this.setUrl(A);
        this.SX1V = B;
        this["doLayout"]();
        return this
    },
    uiCls: "mini-panel",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-panel";
        var _ = "<div class=\"mini-panel-border\">" + "<div class=\"mini-panel-header\" ><div class=\"mini-panel-header-inner\" ><span class=\"mini-panel-icon\"></span><div class=\"mini-panel-title\" ></div><div class=\"mini-tools\" ></div></div></div>" + "<div class=\"mini-panel-viewport\">" + "<div class=\"mini-panel-toolbar\"></div>" + "<div class=\"mini-panel-body\" ></div>" + "<div class=\"mini-panel-footer\"></div>" + "<div class=\"mini-panel-resizeGrid\"></div>" + "</div>" + "</div>";
        this.el.innerHTML = _;
        this._firstChild = this.el.firstChild;
        this.VEE = this._firstChild.firstChild;
        this.ATO = this._firstChild.lastChild;
        this.MuKm = mini.byClass("mini-panel-toolbar", this.el);
        this.$kJ = mini.byClass("mini-panel-body", this.el);
        this.Aca = mini.byClass("mini-panel-footer", this.el);
        this.Zjf = mini.byClass("mini-panel-resizeGrid", this.el);
        var $ = mini.byClass("mini-panel-header-inner", this.el);
        this.Xk = mini.byClass("mini-panel-icon", this.el);
        this.S1 = mini.byClass("mini-panel-title", this.el);
        this.JSQ0 = mini.byClass("mini-tools", this.el);
        BIT(this.$kJ, this.bodyStyle);
        this["doUpdate"]()
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "click", this.YY, this)
        },
		this)
    },
    doUpdate: function () {
        this.S1.innerHTML = this.title;
        this.Xk.style.display = (this.iconCls || this["iconStyle"]) ? "inline" : "none";
        this.Xk.className = "mini-panel-icon " + this.iconCls;
        BIT(this.Xk, this["iconStyle"]);
        this.VEE.style.display = this.showHeader ? "" : "none";
        this.MuKm.style.display = this["showToolbar"] ? "" : "none";
        this.Aca.style.display = this["showFooter"] ? "" : "none";
        var A = "";
        for (var $ = this.buttons.length - 1; $ >= 0; $--) {
            var _ = this.buttons[$];
            A += "<span id=\"" + $ + "\" class=\"" + _.cls + " " + (_.enabled ? "" : "mini-disabled") + "\" style=\"" + _.style + ";" + (_.visible ? "" : "display:none;") + "\"></span>"
        }
        this.JSQ0.innerHTML = A;
        this["doLayout"]()
    },
    count: 1,
    doLayout: function () {
        if (!this.canLayout()) return;
        this.Zjf.style.display = this["allowResize"] ? "" : "none";
        this.$kJ.style.height = "";
        this.$kJ.style.width = "";
        this.VEE.style.width = "";
        this.ATO.style.width = "";
        var F = this["isAutoHeight"](),
		C = this["isAutoWidth"](),
		_ = M32(this.$kJ),
		G = Ly5(this.$kJ),
		J = OFY(this.$kJ),
		$ = this["getWidth"](true),
		E = $;
        $ = $ - J.left - J.right;
        if (jQuery.boxModel) $ = $ - _.left - _.right - G.left - G.right;
        if ($ < 0) $ = 0;
        this.$kJ.style.width = $ + "px";
        $ = E;
        this.VEE.style.width = $ + "px";
        this.MuKm.style.width = $ + "px";
        this.Aca.style.width = "auto";
        if (!F) {
            var I = Ly5(this._firstChild),
			A = this["getHeight"](true),
			B = this.showHeader ? jQuery(this.VEE).outerHeight() : 0,
			D = this["showToolbar"] ? jQuery(this.MuKm).outerHeight() : 0,
			H = this["showFooter"] ? jQuery(this.Aca).outerHeight() : 0;
            this.ATO.style.height = (A - B) + "px";
            A = A - B - D - H;
            if (jQuery.boxModel) A = A - _.top - _.bottom - G.top - G.bottom;
            A = A - J.top - J.bottom;
            if (A < 0) A = 0;
            this.$kJ.style.height = A + "px"
        }
        mini.layout(this._firstChild)
    },
    setHeaderStyle: function ($) {
        this.headerStyle = $;
        BIT(this.VEE, $);
        this["doLayout"]()
    },
    getHeaderStyle: function () {
        return this.headerStyle
    },
    setBodyStyle: function ($) {
        this.bodyStyle = $;
        BIT(this.$kJ, $);
        this["doLayout"]()
    },
    getBodyStyle: function () {
        return this.bodyStyle
    },
    setToolbarStyle: function ($) {
        this.toolbarStyle = $;
        BIT(this.MuKm, $);
        this["doLayout"]()
    },
    getToolbarStyle: function () {
        return this.toolbarStyle
    },
    setFooterStyle: function ($) {
        this.footerStyle = $;
        BIT(this.Aca, $);
        this["doLayout"]()
    },
    getFooterStyle: function () {
        return this.footerStyle
    },
    setHeaderCls: function ($) {
        jQuery(this.VEE)["removeClass"](this.headerCls);
        jQuery(this.VEE)["addClass"]($);
        this.headerCls = $;
        this["doLayout"]()
    },
    getHeaderCls: function () {
        return this.headerCls
    },
    setBodyCls: function ($) {
        jQuery(this.$kJ)["removeClass"](this.bodyCls);
        jQuery(this.$kJ)["addClass"]($);
        this.bodyCls = $;
        this["doLayout"]()
    },
    getBodyCls: function () {
        return this.bodyCls
    },
    setToolbarCls: function ($) {
        jQuery(this.MuKm)["removeClass"](this.toolbarCls);
        jQuery(this.MuKm)["addClass"]($);
        this.toolbarCls = $;
        this["doLayout"]()
    },
    getToolbarCls: function () {
        return this.toolbarCls
    },
    setFooterCls: function ($) {
        jQuery(this.Aca)["removeClass"](this.footerCls);
        jQuery(this.Aca)["addClass"]($);
        this.footerCls = $;
        this["doLayout"]()
    },
    getFooterCls: function () {
        return this.footerCls
    },
    setTitle: function ($) {
        this.title = $;
        this["doUpdate"]()
    },
    getTitle: function () {
        return this.title
    },
    setIconCls: function ($) {
        this.iconCls = $;
        this["doUpdate"]()
    },
    getIconCls: function () {
        return this.iconCls
    },
    setShowCloseButton: function ($) {
        this["showCloseButton"] = $;
        var _ = this.getButton("close");
        _.visible = $;
        if (_) this["doUpdate"]()
    },
    getShowCloseButton: function () {
        return this["showCloseButton"]
    },
    setCloseAction: function ($) {
        this["closeAction"] = $
    },
    getCloseAction: function () {
        return this["closeAction"]
    },
    setShowCollapseButton: function ($) {
        this["showCollapseButton"] = $;
        var _ = this.getButton("collapse");
        _.visible = $;
        if (_) this["doUpdate"]()
    },
    getShowCollapseButton: function () {
        return this["showCollapseButton"]
    },
    setShowHeader: function ($) {
        this.showHeader = $;
        this["doUpdate"]()
    },
    getShowHeader: function () {
        return this.showHeader
    },
    setShowToolbar: function ($) {
        this["showToolbar"] = $;
        this["doUpdate"]()
    },
    getShowToolbar: function () {
        return this["showToolbar"]
    },
    setShowFooter: function ($) {
        this["showFooter"] = $;
        this["doUpdate"]()
    },
    getShowFooter: function () {
        return this["showFooter"]
    },
    YY: function (A) {
        var $ = MRj9(A.target, "mini-tools");
        if ($) {
            var _ = this.getButton(parseInt(A.target.id));
            if (_) this.QN(_, A)
        }
    },
    QN: function (B, $) {
        var C = {
            button: B,
            index: this.buttons.indexOf(B),
            name: B.name.toLowerCase(),
            htmlEvent: $,
            cancel: false
        };
        this.fire("beforebuttonclick", C);
        try {
            if (C.name == "close" && this["closeAction"] == "destroy" && this.AeO && this.AeO.contentWindow) {
                var _ = true;
                if (this.AeO.contentWindow.CloseWindow) _ = this.AeO.contentWindow.CloseWindow("close");
                else if (this.AeO.contentWindow.CloseOwnerWindow) _ = this.AeO.contentWindow.CloseOwnerWindow("close");
                if (_ === false) C.cancel = true
            }
        } catch (A) { }
        if (C.cancel == true) return C;
        this.fire("buttonclick", C);
        if (C.name == "close") if (this["closeAction"] == "destroy") {
            this.__HideAction = "close";
            this["destroy"]()
        } else this.hide();
        if (C.name == "collapse") {
            this.toggle();
            if (this["refreshOnExpand"] && this.expanded && this.url) this.reload()
        }
        return C
    },
    onButtonClick: function (_, $) {
        this.on("buttonclick", _, $)
    },
    DEv3: function () {
        this.buttons = [];
        var _ = this.createButton({
            name: "close",
            cls: "mini-tools-close",
            visible: this["showCloseButton"]
        });
        this.buttons.push(_);
        var $ = this.createButton({
            name: "collapse",
            cls: "mini-tools-collapse",
            visible: this["showCollapseButton"]
        });
        this.buttons.push($)
    },
    createButton: function (_) {
        var $ = mini.copyTo({
            name: "",
            cls: "",
            style: "",
            visible: true,
            enabled: true,
            html: ""
        },
		_);
        return $
    },
    addButton: function (_, $) {
        if (typeof _ == "string") _ = {
            iconCls: _
        };
        _ = this.createButton(_);
        if (typeof $ != "number") $ = this.buttons.length;
        this.buttons.insert($, _);
        this["doUpdate"]()
    },
    updateButton: function ($, A) {
        var _ = this.getButton($);
        if (!_) return;
        mini.copyTo(_, A);
        this["doUpdate"]()
    },
    removeButton: function ($) {
        var _ = this.getButton($);
        if (!_) return;
        this.buttons.remove(_);
        this["doUpdate"]()
    },
    getButton: function ($) {
        if (typeof $ == "number") return this.buttons[$];
        else for (var _ = 0, A = this.buttons.length; _ < A; _++) {
            var B = this.buttons[_];
            if (B.name == $) return B
        }
    },
    destroy: function ($) {
        this.YN();
        this.AeO = null;
        this.MuKm = null;
        this.$kJ = null;
        this.Aca = null;
        mini.Panel["superclass"]["destroy"]["call"](this, $)
    },
    setBody: function (_) {
        if (!_) return;
        if (!mini.isArray(_)) _ = [_];
        for (var $ = 0, A = _.length; $ < A; $++) {
            var B = _[$];
            mini.append(this.$kJ, B)
        }
        mini.parse(this.$kJ);
        this["doLayout"]()
    },
    set_bodyParent: function ($) { },
    setToolbar: function (_) {
        if (!_) return;
        if (!mini.isArray(_)) _ = [_];
        for (var $ = 0, A = _.length; $ < A; $++) mini.append(this.MuKm, _[$]);
        mini.parse(this.MuKm);
        this["doLayout"]()
    },
    setFooter: function (_) {
        if (!_) return;
        if (!mini.isArray(_)) _ = [_];
        for (var $ = 0, A = _.length; $ < A; $++) mini.append(this.Aca, _[$]);
        mini.parse(this.Aca);
        this["doLayout"]()
    },
    getHeaderEl: function () {
        return this.VEE
    },
    getToolbarEl: function () {
        return this.MuKm
    },
    getBodyEl: function () {
        return this.$kJ
    },
    getFooterEl: function () {
        return this.Aca
    },
    getIFrameEl: function ($) {
        return this.AeO
    },
    Gjy: function () {
        return this.$kJ
    },
    YN: function ($) {
        if (this.AeO) {
            var _ = this.AeO;
            _.src = "";
            if (_._ondestroy) _._ondestroy();
            try {
                this.AeO.parentNode.removeChild(this.AeO);
                this.AeO["removeNode"](true)
            } catch (A) { }
        }
        this.AeO = null;
        try {
            CollectGarbage()
        } catch (B) { }
        if ($ === true) mini.removeChilds(this.$kJ)
    },
    Jo: 80,
    Sc: function () {
        this.YN(true);
        var A = new Date(),
		$ = this;
        this.loadedUrl = this.url;
        if (this.maskOnLoad) this.loading();
        var _ = mini.createIFrame(this.url,
		function (_, C) {
		    var B = (A - new Date()) + $.Jo;
		    if (B < 0) B = 0;
		    setTimeout(function () {
		        $.unmask()
		    },
			B);
		    try {
		        $.AeO.contentWindow.Owner = $.Owner;
		        $.AeO.contentWindow.CloseOwnerWindow = function (_) {
		            $.__HideAction = _;
		            var A = true;
		            if ($.__onDestroy) A = $.__onDestroy(_);
		            if (A === false) return false;
		            var B = {
		                iframe: $.AeO,
		                action: _
		            };
		            $.fire("unload", B);
		            setTimeout(function () {
		                $["destroy"]()
		            },
					10)
		        }
		    } catch (D) { }
		    if (C) {
		        if ($.__onLoad) $.__onLoad();
		        var D = {
		            iframe: $.AeO
		        };
		        $.fire("load", D)
		    }
		});
        this.$kJ.appendChild(_);
        this.AeO = _
    },
    load: function (_, $, A) {
        this.setUrl(_, $, A)
    },
    reload: function () {
        this.setUrl(this.url)
    },
    setUrl: function ($, _, A) {
        this.url = $;
        this.__onLoad = _;
        this.__onDestroy = A;
        if (this.expanded) this.Sc()
    },
    getUrl: function () {
        return this.url
    },
    setRefreshOnExpand: function ($) {
        this["refreshOnExpand"] = $
    },
    getRefreshOnExpand: function () {
        return this["refreshOnExpand"]
    },
    setMaskOnLoad: function ($) {
        this.maskOnLoad = $
    },
    getMaskOnLoad: function ($) {
        return this.maskOnLoad
    },
    expanded: true,
    setExpanded: function ($) {
        if (this.expanded != $) {
            this.expanded = $;
            if (this.expanded) this.expand();
            else this.collapse()
        }
    },
    toggle: function () {
        if (this.expanded) this.collapse();
        else this.expand()
    },
    collapse: function () {
        this.expanded = false;
        this._height = this.el.style.height;
        this.el.style.height = "auto";
        this.ATO.style.display = "none";
        RE(this.el, "mini-panel-collapse");
        this["doLayout"]()
    },
    expand: function () {
        this.expanded = true;
        this.el.style.height = this._height;
        this.ATO.style.display = "block";
        delete this._height;
        WeL(this.el, "mini-panel-collapse");
        if (this.url && this.url != this.loadedUrl) this.Sc();
        this["doLayout"]()
    },
    getAttrs: function (_) {
        var D = mini.Panel["superclass"]["getAttrs"]["call"](this, _);
        mini["_ParseString"](_, D, ["title", "iconCls", "iconStyle", "headerCls", "headerStyle", "bodyCls", "bodyStyle", "footerCls", "footerStyle", "toolbarCls", "toolbarStyle", "footer", "toolbar", "url", "closeAction", "loadingMsg", "beforebuttonclick", "buttonclick", "load"]);
        mini["_ParseBool"](_, D, ["allowResize", "showCloseButton", "showHeader", "showToolbar", "showFooter", "showCollapseButton", "refreshOnExpand", "maskOnLoad", "expanded"]);
        var C = mini["getChildNodes"](_, true);
        for (var $ = C.length - 1; $ >= 0; $--) {
            var B = C[$],
			A = jQuery(B).attr("property");
            if (!A) continue;
            A = A.toLowerCase();
            if (A == "toolbar") D.toolbar = B;
            else if (A == "footer") D.footer = B
        }
        D.body = C;
        return D
    }
});
HbyG(mini.Panel, "panel");
mini.Window = function () {
    mini.Window["superclass"]["constructor"]["call"](this);
    this["addCls"]("mini-window");
    this["setVisible"](false);
    this.setAllowDrag(this.allowDrag);
    this.setAllowResize(this["allowResize"])
};
Pv_r(mini.Window, mini.Panel, {
    x: 0,
    y: 0,
    state: "restore",
    MSr: "mini-window-drag",
    P34: "mini-window-resize",
    allowDrag: true,
    allowResize: false,
    showCloseButton: true,
    showMaxButton: false,
    showMinButton: false,
    showCollapseButton: false,
    showModal: true,
    minWidth: 150,
    minHeight: 80,
    maxWidth: 2000,
    maxHeight: 2000,
    uiCls: "mini-window",
    _create: function () {
        mini.Window["superclass"]["_create"]["call"](this)
    },
    DEv3: function () {
        this.buttons = [];
        var btnClose = this.createButton({
            name: "close",
            cls: "mini-tools-close",
            visible: this["showCloseButton"]
        });
        this.buttons.push(btnClose);
        var btnMax = this.createButton({
            name: "max",
            cls: "mini-tools-max",
            visible: this["showMaxButton"]
        });
        this.buttons.push(btnMax);
        var btnMin = this.createButton({
            name: "min",
            cls: "mini-tools-min",
            visible: this["showMinButton"]
        });
        this.buttons.push(btnMin);
        var btnCollapse = this.createButton({
            name: "collapse",
            cls: "mini-tools-collapse",
            visible: this["showCollapseButton"]
        });
        this.buttons.push(btnCollapse)
    },
    _initEvents: function () {
        mini.Window["superclass"]["_initEvents"]["call"](this);
        KJ_T(function () {
            VNV(this.el, "mouseover", this.Vv5, this);
            VNV(window, "resize", this.CZh, this);
            VNV(this.el, "mousedown", this.UN, this)
        },
		this)
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        if (this.state == "max") {
            var $ = this.getParentBox();
            this.el.style.left = "0px";
            this.el.style.top = "0px";
            mini.setSize(this.el, $.width, $.height)
        }
        mini.Window["superclass"]["doLayout"]["call"](this);
        if (this.allowDrag) RE(this.el, this.MSr);
        if (this.state == "max") {
            this.Zjf.style.display = "none";
            WeL(this.el, this.MSr)
        }
        this.T3d()
    },
    T3d: function () {
        var A = this["showModal"] && this["isDisplay"]();
        if (!this.T4) this.T4 = mini.append(document.body, "<div class=\"mini-modal\" style=\"display:none\"></div>");
        function $() {
            mini["repaint"](document.body);
            var root = document.documentElement,
			_scrollWidth = parseInt(Math.max(document.body.scrollWidth, root ? root.scrollWidth : 0)),
			_scrollHeight = parseInt(Math.max(document.body.scrollHeight, root ? root.scrollHeight : 0)),
			D = mini.getViewportBox(),
			C = D.height;
            if (C < _scrollHeight) C = _scrollHeight;
            var _ = D.width;
            if (_ < _scrollWidth) _ = _scrollWidth;
            this.T4.style.display = A ? "block" : "none";
            this.T4.style.height = C + "px";
            this.T4.style.width = _ + "px";
            this.T4.style.zIndex = YI(this.el, "zIndex") - 1
        }
        if (A) {
            var _ = this;
            setTimeout(function () {
                _.T4.style.display = "none";
                $["call"](_)
            },
			1)
        } else this.T4.style.display = "none"
    },
    getParentBox: function () {
        var $ = mini.getViewportBox(),
		_ = this.Q6Y1 || document.body;
        if (_ != document.body) $ = EcG(_);
        return $
    },
    setShowModal: function ($) {
        this["showModal"] = $
    },
    getShowModal: function () {
        return this["showModal"]
    },
    setMinWidth: function ($) {
        if (isNaN($)) return;
        this.minWidth = $
    },
    getMinWidth: function () {
        return this.minWidth
    },
    setMinHeight: function ($) {
        if (isNaN($)) return;
        this.minHeight = $
    },
    getMinHeight: function () {
        return this.minHeight
    },
    setMaxWidth: function ($) {
        if (isNaN($)) return;
        this.maxWidth = $
    },
    getMaxWidth: function () {
        return this.maxWidth
    },
    setMaxHeight: function ($) {
        if (isNaN($)) return;
        this.maxHeight = $
    },
    getMaxHeight: function () {
        return this.maxHeight
    },
    setAllowDrag: function ($) {
        this.allowDrag = $;
        WeL(this.el, this.MSr);
        if ($) RE(this.el, this.MSr)
    },
    getAllowDrag: function () {
        return this.allowDrag
    },
    setAllowResize: function ($) {
        if (this["allowResize"] != $) {
            this["allowResize"] = $;
            this["doLayout"]()
        }
    },
    getAllowResize: function () {
        return this["allowResize"]
    },
    setShowMaxButton: function ($) {
        this["showMaxButton"] = $;
        var _ = this.getButton("max");
        _.visible = $;
        if (_) this["doUpdate"]()
    },
    getShowMaxButton: function () {
        return this["showMaxButton"]
    },
    setShowMinButton: function ($) {
        this["showMinButton"] = $;
        var _ = this.getButton("min");
        _.visible = $;
        if (_) this["doUpdate"]()
    },
    getShowMinButton: function () {
        return this["showMinButton"]
    },
    max: function () {
        this.state = "max";
        this.show();
        var $ = this.getButton("max");
        if ($) {
            $.cls = "mini-tools-restore";
            this["doUpdate"]()
        }
    },
    restore: function () {
        this.state = "restore";
        this.show(this.x, this.y);
        var $ = this.getButton("max");
        if ($) {
            $.cls = "mini-tools-max";
            this["doUpdate"]()
        }
    },
    containerEl: null,
    show: function (B, _) {
        this.SX1V = false;
        var A = this.Q6Y1 || document.body;
        if (!this.isRender() || this.el.parentNode != A) this["render"](A);
        this.el.style.zIndex = mini.getMaxZIndex();
        this.YTFf(B, _);
        this.SX1V = true;
        this["setVisible"](true);
        if (this.state != "max") {
            var $ = EcG(this.el);
            this.x = $.x;
            this.y = $.y
        }
        try {
            this.el.focus()
        } catch (C) { }
    },
    hide: function () {
        this["setVisible"](false);
        this.T3d()
    },
    UdCU: function () {
        this.el.style.display = "";
        var $ = EcG(this.el);
        if ($.width > this.maxWidth) {
            SI9N(this.el, this.maxWidth);
            $ = EcG(this.el)
        }
        if ($.height > this.maxHeight) {
            Quj1(this.el, this.maxHeight);
            $ = EcG(this.el)
        }
        if ($.width < this.minWidth) {
            SI9N(this.el, this.minWidth);
            $ = EcG(this.el)
        }
        if ($.height < this.minHeight) {
            Quj1(this.el, this.minHeight);
            $ = EcG(this.el)
        }
    },
    YTFf: function (B, A) {
        var _ = this.getParentBox();
        if (this.state == "max") {
            if (!this._width) {
                var $ = EcG(this.el);
                this._width = $.width;
                this._height = $.height;
                this.x = $.x;
                this.y = $.y
            }
        } else {
            if (mini.isNull(B)) B = "center";
            if (mini.isNull(A)) A = "middle";
            this.el.style.position = "absolute";
            this.el.style.left = "-2000px";
            this.el.style.top = "-2000px";
            this.el.style.display = "";
            if (this._width) {
                this["setWidth"](this._width);
                this["setHeight"](this._height)
            }
            this.UdCU();
            $ = EcG(this.el);
            if (B == "left") B = 0;
            if (B == "center") B = _.width / 2 - $.width / 2;
            if (B == "right") B = _.width - $.width;
            if (A == "top") A = 0;
            if (A == "middle") A = _.y + _.height / 2 - $.height / 2;
            if (A == "bottom") A = _.height - $.height;
            if (B + $.width > _.right) B = _.right - $.width;
            if (A + $.height > _.bottom) A = _.bottom - $.height;
            if (B < 0) B = 0;
            if (A < 0) A = 0;
            this.el.style.display = "";
            mini.setX(this.el, B);
            mini.setY(this.el, A);
            this.el.style.left = B + "px";
            this.el.style.top = A + "px"
        }
        this["doLayout"]()
    },
    QN: function (_, $) {
        var A = mini.Window["superclass"].QN["call"](this, _, $);
        if (A.cancel == true) return A;
        if (A.name == "max") if (this.state == "max") this.restore();
        else this.max();
        return A
    },
    CZh: function ($) {
        if (this.state == "max") this["doLayout"]();
        if (!mini.isIE6) this.T3d()
    },
    UN: function (B) {
        var _ = this;
        if (this.state != "max" && this.allowDrag && Yma(this.VEE, B.target) && !MRj9(B.target, "mini-tools")) {
            var _ = this,
			A = this.getBox(),
			$ = new mini.Drag({
			    capture: false,
			    onStart: function () {
			        _.SinP = mini.append(document.body, "<div class=\"mini-resizer-mask\"></div>");
			        _.YL5C = mini.append(document.body, "<div class=\"mini-drag-proxy\"></div>")
			    },
			    onMove: function (B) {
			        var F = B.now[0] - B.init[0],
					E = B.now[1] - B.init[1];
			        F = A.x + F;
			        E = A.y + E;
			        var D = _.getParentBox(),
					$ = F + A.width,
					C = E + A.height;
			        if ($ > D.width) F = D.width - A.width;
			        if (F < 0) F = 0;
			        if (E < 0) E = 0;
			        _.x = F;
			        _.y = E;
			        var G = {
			            x: F,
			            y: E,
			            width: A.width,
			            height: A.height
			        };
			        Ggi(_.YL5C, G)
			    },
			    onStop: function () {
			        var $ = EcG(_.YL5C);
			        Ggi(_.el, $);
			        jQuery(_.SinP).remove();
			        _.SinP = null;
			        jQuery(_.YL5C).remove();
			        _.YL5C = null
			    }
			});
            $.start(B)
        }
        if (Yma(this.Zjf, B.target) && this["allowResize"]) {
            $ = this.G0$f();
            $.start(B)
        }
    },
    G0$f: function () {
        if (!this._resizeDragger) this._resizeDragger = new mini.Drag({
            capture: true,
            onStart: mini.createDelegate(this.J_sA, this),
            onMove: mini.createDelegate(this.KsZ, this),
            onStop: mini.createDelegate(this.ZYd, this)
        });
        return this._resizeDragger
    },
    J_sA: function ($) {
        this.proxy = mini.append(document.body, "<div class=\"mini-windiw-resizeProxy\"></div>");
        this.proxy.style.cursor = "se-resize";
        this.elBox = EcG(this.el);
        Ggi(this.proxy, this.elBox)
    },
    KsZ: function (A) {
        var C = A.now[0] - A.init[0],
		$ = A.now[1] - A.init[1],
		_ = this.elBox.width + C,
		B = this.elBox.height + $;
        if (_ < this.minWidth) _ = this.minWidth;
        if (B < this.minHeight) B = this.minHeight;
        if (_ > this.maxWidth) _ = this.maxWidth;
        if (B > this.maxHeight) B = this.maxHeight;
        mini.setSize(this.proxy, _, B)
    },
    ZYd: function ($) {
        var _ = EcG(this.proxy);
        jQuery(this.proxy).remove();
        this.proxy = null;
        this.elBox = null;
        this["setWidth"](_.width);
        this["setHeight"](_.height);
        delete this._width;
        delete this._height
    },
    destroy: function ($) {
        BP(window, "resize", this.CZh, this);
        if (this.T4) {
            jQuery(this.T4).remove();
            this.T4 = null
        }
        if (this.shadowEl) {
            jQuery(this.shadowEl).remove();
            this.shadowEl = null
        }
        mini.Window["superclass"]["destroy"]["call"](this, $)
    },
    getAttrs: function ($) {
        var _ = mini.Window["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["modalStyle"]);
        mini["_ParseBool"]($, _, ["showModal", "showShadow", "allowDrag", "allowResize", "showMaxButton", "showMinButton"]);
        mini["_ParseInt"]($, _, ["minWidth", "minHeight", "maxWidth", "maxHeight"]);
        return _
    }
});
HbyG(mini.Window, "window");
mini.MessageBox = {
    alertTitle: "\u63d0\u9192",
    confirmTitle: "\u786e\u8ba4",
    prompTitle: "\u8f93\u5165",
    prompMessage: "\u8bf7\u8f93\u5165\u5185\u5bb9\uff1a",
    buttonText: {
        ok: "\u786e\u5b9a",
        cancel: "\u53d6\u6d88",
        yes: "\u662f",
        no: "\u5426"
    },
    show: function (F) {
        F = mini.copyTo({
            width: "auto",
            height: "auto",
            showModal: true,
            minWidth: 150,
            maxWidth: 800,
            minHeight: 100,
            maxHeight: 350,
            title: "",
            titleIcon: "",
            iconCls: "",
            iconStyle: "",
            message: "",
            html: "",
            spaceStyle: "margin-right:15px",
            showCloseButton: true,
            buttons: null,
            buttonWidth: 55,
            callback: null
        },
		F);
        var I = F.callback,
		C = new mini.Window();
        C.setBodyStyle("overflow:hidden");
        C.setShowModal(F["showModal"]);
        C.setTitle(F.title || "");
        C.setIconCls(F.titleIcon);
        C.setShowCloseButton(F["showCloseButton"]);
        var J = C.uid + "$table",
		N = C.uid + "$content",
		L = "<div class=\"" + F.iconCls + "\" style=\"" + F["iconStyle"] + "\"></div>",
		Q = "<table class=\"mini-messagebox-table\" id=\"" + J + "\" style=\"\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>" + L + "</td><td id=\"" + N + "\" style=\"text-align:center;padding:8px;padding-left:0;\">" + (F.message || "") + "</td></tr></table>",
		_ = "<div class=\"mini-messagebox-content\"></div>" + "<div class=\"mini-messagebox-buttons\"></div>";
        C.$kJ.innerHTML = _;
        var M = C.$kJ.firstChild;
        if (F.html) {
            if (typeof F.html == "string") M.innerHTML = F.html;
            else if (mini.isElement(F.html)) M.appendChild(F.html)
        } else M.innerHTML = Q;
        C._Buttons = [];
        var P = C.$kJ.lastChild;
        if (F.buttons && F.buttons.length > 0) {
            for (var H = 0, D = F.buttons.length; H < D; H++) {
                var E = F.buttons[H],
				K = mini.MessageBox.buttonText[E],
				$ = new mini.Button();
                $["setText"](K);
                $["setWidth"](F.buttonWidth);
                $["render"](P);
                $.action = E;
                $.on("click",
				function (_) {
				    var $ = _.sender;
				    if (I) I($.action);
				    mini.MessageBox.hide(C)
				});
                if (H != D - 1) $.setStyle(F.spaceStyle);
                C._Buttons.push($)
            }
        } else P.style.display = "none";
        C.setMinWidth(F.minWidth);
        C.setMinHeight(F.minHeight);
        C.setMaxWidth(F.maxWidth);
        C.setMaxHeight(F.maxHeight);
        C["setWidth"](F.width);
        C["setHeight"](F.height);
        C.show();
        var A = C["getWidth"]();
        C["setWidth"](A);
        var B = document.getElementById(J);
        if (B) B.style.width = "100%";
        var G = document.getElementById(N);
        if (G) G.style.width = "100%";
        var O = C._Buttons[0];
        if (O) O.focus();
        else C.focus();
        C.on("beforebuttonclick",
		    function ($) {
		        if (I) I("close");
		        $.cancel = true;
		        mini.MessageBox.hide(C)
		    }
        );
        VNV(C.el, "keydown",
		    function ($) {
		        if ($.keyCode == 27) {
		            if (I) I("close");
		            $.cancel = true;
		            mini.MessageBox.hide(C)
		        }
		    }
        );
        return C.uid
    },
    hide: function (C) {
        if (!C) return;
        var _ = typeof C == "object" ? C : mini.getbyUID(C);
        if (!_) return;
        for (var $ = 0, A = _._Buttons.length; $ < A; $++) {
            var B = _._Buttons[$];
            B["destroy"]()
        }
        _._Buttons = null;
        _["destroy"]()
    },
    alert: function (message, title, callback) {
        return mini.MessageBox.show({
            minWidth: 250,
            title: title || mini.MessageBox.alertTitle,
            buttons: ["ok"],
            message: message,
            iconCls: "mini-messagebox-warning",
            callback: callback
        })
    },
    confirm: function (message, title, callback) {
        return mini.MessageBox.show({
            minWidth: 250,
            title: title || mini.MessageBox.confirmTitle,
            buttons: ["ok", "cancel"],
            message: message,
            iconCls: "mini-messagebox-question",
            callback: callback
        })
    },
    prompt: function (C, B, A, _) {
        var F = "prompt$" + new Date().getTime(),
		E = C || mini.MessageBox.promptMessage;
        if (_) E = E + "<br/><textarea id=\"" + F + "\" style=\"width:200px;height:60px;margin-top:3px;\"></textarea>";
        else E = E + "<br/><input id=\"" + F + "\" type=\"text\" style=\"width:200px;margin-top:3px;\"/>";
        var D = mini.MessageBox.show({
            title: B || mini.MessageBox.promptTitle,
            buttons: ["ok", "cancel"],
            width: 250,
            html: "<div style=\"padding:5px;padding-left:10px;\">" + E + "</div>",
            callback: function (_) {
                var $ = document.getElementById(F);
                if (A) A(_, $.value)
            }
        }),
		$ = document.getElementById(F);
        $.focus();
        return D
    },
    loading: function (_, $) {
        return mini.MessageBox.show({
            minHeight: 50,
            title: $,
            showCloseButton: false,
            message: _,
            iconCls: "mini-messagebox-waiting"
        })
    }
};
mini.alert = mini.MessageBox.alert;
mini.confirm = mini.MessageBox.confirm;
mini.prompt = mini.MessageBox.prompt;
mini.loading = mini.MessageBox.loading;
mini.showMessageBox = mini.MessageBox.show;
mini.hideMessageBox = mini.MessageBox.hide;
mini.Splitter = function () {
    this.UC8s();
    mini.Splitter["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Splitter, mini.Control, {
    width: 300,
    height: 180,
    vertical: false,
    allowResize: true,
    pane1: null,
    pane2: null,
    showHandleButton: true,
    handlerStyle: "",
    handlerCls: "",
    handlerSize: 6,
    uiCls: "mini-splitter",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-splitter";
        this.el.innerHTML = "<div class=\"mini-splitter-border\"><div id=\"1\" class=\"mini-splitter-pane mini-splitter-pane1\"></div><div id=\"2\" class=\"mini-splitter-pane mini-splitter-pane2\"></div><div class=\"mini-splitter-handler\"></div></div>";
        this._firstChild = this.el.firstChild;
        this.PVJ = this._firstChild.firstChild;
        this._J9 = this._firstChild.childNodes[1];
        this.VU = this._firstChild.lastChild
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "click", this.YY, this);
            VNV(this.el, "mousedown", this.UNAE, this)
        },
		this)
    },
    UC8s: function () {
        this.pane1 = {
            index: 1,
            minSize: 30,
            maxSize: 3000,
            size: "",
            showCollapseButton: false,
            cls: "",
            style: "",
            visible: true,
            expanded: true
        };
        this.pane2 = mini.copyTo({},
		this.pane1);
        this.pane2.index = 2
    },
    doUpdate: function () {
        this["doLayout"]()
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        this.VU.style.cursor = this["allowResize"] ? "" : "default";
        WeL(this.el, "mini-splitter-vertical");
        if (this.vertical) RE(this.el, "mini-splitter-vertical");
        WeL(this.PVJ, "mini-splitter-pane1-vertical");
        WeL(this._J9, "mini-splitter-pane2-vertical");
        if (this.vertical) {
            RE(this.PVJ, "mini-splitter-pane1-vertical");
            RE(this._J9, "mini-splitter-pane2-vertical")
        }
        WeL(this.VU, "mini-splitter-handler-vertical");
        if (this.vertical) RE(this.VU, "mini-splitter-handler-vertical");
        BIT(this.PVJ, this.pane1.style);
        BIT(this._J9, this.pane2.style);
        var B = this["getHeight"](true),
		_ = this["getWidth"](true);
        if (!jQuery.boxModel) {
            var Q = Ly5(this._firstChild);
            B = B + Q.top + Q.bottom;
            _ = _ + Q.left + Q.right
        }
        this._firstChild.style.width = _ + "px";
        this._firstChild.style.height = B + "px";
        var $ = this.PVJ,
		C = this._J9,
		G = jQuery($),
		I = jQuery(C);
        $.style.display = C.style.display = this.VU.style.display = "";
        var D = this["handlerSize"];
        this.pane1.size = String(this.pane1.size);
        this.pane2.size = String(this.pane2.size);
        var F = parseFloat(this.pane1.size),
		H = parseFloat(this.pane2.size),
		O = isNaN(F),
		T = isNaN(H),
		N = !isNaN(F) && this.pane1.size.indexOf("%") != -1,
		R = !isNaN(H) && this.pane2.size.indexOf("%") != -1,
		J = !O && !N,
		M = !T && !R,
		P = this.vertical ? B - this["handlerSize"] : _ - this["handlerSize"],
		K = p2Size = 0;
        if (O || T) {
            if (O && T) {
                K = parseInt(P / 2);
                p2Size = P - K
            } else if (J) {
                K = F;
                p2Size = P - K
            } else if (N) {
                K = parseInt(P * F / 100);
                p2Size = P - K
            } else if (M) {
                p2Size = H;
                K = P - p2Size
            } else if (R) {
                p2Size = parseInt(P * H / 100);
                K = P - p2Size
            }
        } else if (N && M) {
            p2Size = H;
            K = P - p2Size
        } else if (J && R) {
            K = F;
            p2Size = P - K
        } else {
            var L = F + H;
            K = parseInt(P * F / L);
            p2Size = P - K
        }
        if (K > this.pane1.maxSize) {
            K = this.pane1.maxSize;
            p2Size = P - K
        }
        if (p2Size > this.pane2.maxSize) {
            p2Size = this.pane2.maxSize;
            K = P - p2Size
        }
        if (K < this.pane1.minSize) {
            K = this.pane1.minSize;
            p2Size = P - K
        }
        if (p2Size < this.pane2.minSize) {
            p2Size = this.pane2.minSize;
            K = P - p2Size
        }
        if (this.pane1.expanded == false) {
            p2Size = P;
            K = 0;
            $.style.display = "none"
        } else if (this.pane2.expanded == false) {
            K = P;
            p2Size = 0;
            C.style.display = "none"
        }
        if (this.pane1.visible == false) {
            p2Size = P + D;
            K = D = 0;
            $.style.display = "none";
            this.VU.style.display = "none"
        } else if (this.pane2.visible == false) {
            K = P + D;
            p2Size = D = 0;
            C.style.display = "none";
            this.VU.style.display = "none"
        }
        if (this.vertical) {
            SI9N($, _);
            SI9N(C, _);
            Quj1($, K);
            Quj1(C, p2Size);
            C.style.top = (K + D) + "px";
            this.VU.style.left = "0px";
            this.VU.style.top = K + "px";
            SI9N(this.VU, _);
            Quj1(this.VU, this["handlerSize"]);
            $.style.left = "0px";
            C.style.left = "0px"
        } else {
            SI9N($, K);
            SI9N(C, p2Size);
            Quj1($, B);
            Quj1(C, B);
            C.style.left = (K + D) + "px";
            this.VU.style.top = "0px";
            this.VU.style.left = K + "px";
            SI9N(this.VU, this["handlerSize"]);
            Quj1(this.VU, B);
            $.style.top = "0px";
            C.style.top = "0px"
        }
        var S = "<div class=\"mini-splitter-handler-buttons\">";
        if (!this.pane1.expanded || !this.pane2.expanded) {
            if (!this.pane1.expanded) {
                if (this.pane1["showCollapseButton"]) S += "<a id=\"1\" class=\"mini-splitter-pane2-button\"></a>"
            } else if (this.pane2["showCollapseButton"]) S += "<a id=\"2\" class=\"mini-splitter-pane1-button\"></a>"
        } else {
            if (this.pane1["showCollapseButton"]) S += "<a id=\"1\" class=\"mini-splitter-pane1-button\"></a>";
            if (this["allowResize"]) if ((this.pane1["showCollapseButton"] && this.pane2["showCollapseButton"]) || (!this.pane1["showCollapseButton"] && !this.pane2["showCollapseButton"])) S += "<span class=\"mini-splitter-resize-button\"></span>";
            if (this.pane2["showCollapseButton"]) S += "<a id=\"2\" class=\"mini-splitter-pane2-button\"></a>"
        }
        S += "</div>";
        this.VU.innerHTML = S;
        var E = this.VU.firstChild;
        E.style.display = this.showHandleButton ? "" : "none";
        var A = EcG(E);
        if (this.vertical) E.style.marginLeft = -A.width / 2 + "px";
        else E.style.marginTop = -A.height / 2 + "px";
        if (!this.pane1.visible || !this.pane2.visible || !this.pane1.expanded || !this.pane2.expanded) RE(this.VU, "mini-splitter-nodrag");
        else WeL(this.VU, "mini-splitter-nodrag");
        mini.layout(this._firstChild)
    },
    getPaneBox: function ($) {
        var _ = this.getPaneEl($);
        if (!_) return null;
        return EcG(_)
    },
    getPane: function ($) {
        if ($ == 1) return this.pane1;
        else if ($ == 2) return this.pane2;
        return $
    },
    setPanes: function (_) {
        if (!mini.isArray(_)) return;
        for (var $ = 0; $ < 2; $++) {
            var A = _[$];
            this.updatePane($ + 1, A)
        }
    },
    getPaneEl: function ($) {
        if ($ == 1) return this.PVJ;
        return this._J9
    },
    updatePane: function (_, F) {
        var $ = this.getPane(_);
        if (!$) return;
        mini.copyTo($, F);
        var B = this.getPaneEl(_),
		C = $.body;
        delete $.body;
        if (C) {
            if (!mini.isArray(C)) C = [C];
            for (var A = 0, E = C.length; A < E; A++) mini.append(B, C[A])
        }
        if ($.bodyParent) {
            var D = $.bodyParent;
            while (D.firstChild) B.appendChild(D.firstChild)
        }
        delete $.bodyParent;
        this["doUpdate"]()
    },
    setShowHandleButton: function ($) {
        this.showHandleButton = $;
        this["doUpdate"]()
    },
    getShowHandleButton: function ($) {
        return this.showHandleButton
    },
    setVertical: function ($) {
        this.vertical = $;
        this["doUpdate"]()
    },
    getVertical: function () {
        return this.vertical
    },
    expandPane: function (_) {
        var $ = this.getPane(_);
        if (!$) return;
        $.expanded = true;
        this["doUpdate"]()
    },
    collapsePane: function (_) {
        var $ = this.getPane(_);
        if (!$) return;
        $.expanded = false;
        var A = $ == this.pane1 ? this.pane2 : this.pane1;
        if (A.expanded == false) {
            A.expanded = true;
            A.visible = true
        }
        this["doUpdate"]()
    },
    togglePane: function (_) {
        var $ = this.getPane(_);
        if (!$) return;
        if ($.expanded) this.collapsePane($);
        else this.expandPane($)
    },
    showPane: function (_) {
        var $ = this.getPane(_);
        if (!$) return;
        $.visible = true;
        this["doUpdate"]()
    },
    hidePane: function (_) {
        var $ = this.getPane(_);
        if (!$) return;
        $.visible = false;
        var A = $ == this.pane1 ? this.pane2 : this.pane1;
        if (A.visible == false) {
            A.expanded = true;
            A.visible = true
        }
        this["doUpdate"]()
    },
    setAllowResize: function ($) {
        if (this["allowResize"] != $) {
            this["allowResize"] = $;
            this["doLayout"]()
        }
    },
    getAllowResize: function () {
        return this["allowResize"]
    },
    setHandlerSize: function ($) {
        if (this["handlerSize"] != $) {
            this["handlerSize"] = $;
            this["doLayout"]()
        }
    },
    getHandlerSize: function () {
        return this["handlerSize"]
    },
    YY: function (B) {
        var A = B.target;
        if (!Yma(this.VU, A)) return;
        var _ = parseInt(A.id),
		$ = this.getPane(_),
		B = {
		    pane: $,
		    paneIndex: _,
		    cancel: false
		};
        if ($.expanded) this.fire("beforecollapse", B);
        else this.fire("beforeexpand", B);
        if (B.cancel == true) return;
        if (A.className == "mini-splitter-pane1-button") this.togglePane(_);
        else if (A.className == "mini-splitter-pane2-button") this.togglePane(_)
    },
    QN: function ($, _) {
        this.fire("buttonclick", {
            pane: $,
            index: this.pane1 == $ ? 1 : 2,
            htmlEvent: _
        })
    },
    onButtonClick: function (_, $) {
        this.on("buttonclick", _, $)
    },
    UNAE: function (A) {
        var _ = A.target;
        if (!this["allowResize"]) return;
        if (!this.pane1.visible || !this.pane2.visible || !this.pane1.expanded || !this.pane2.expanded) return;
        if (Yma(this.VU, _)) if (_.className == "mini-splitter-pane1-button" || _.className == "mini-splitter-pane2-button");
        else {
            var $ = this._Fc();
            $.start(A)
        }
    },
    _Fc: function () {
        if (!this.drag) this.drag = new mini.Drag({
            capture: true,
            onStart: mini.createDelegate(this.J_sA, this),
            onMove: mini.createDelegate(this.KsZ, this),
            onStop: mini.createDelegate(this.ZYd, this)
        });
        return this.drag
    },
    J_sA: function ($) {
        this.SinP = mini.append(document.body, "<div class=\"mini-resizer-mask\"></div>");
        this.YL5C = mini.append(document.body, "<div class=\"mini-proxy\"></div>");
        this.YL5C.style.cursor = this.vertical ? "n-resize" : "w-resize";
        this.handlerBox = EcG(this.VU);
        this.elBox = EcG(this._firstChild, true);
        Ggi(this.YL5C, this.handlerBox)
    },
    KsZ: function (C) {
        if (!this.handlerBox) return;
        if (!this.elBox) this.elBox = EcG(this._firstChild, true);
        var B = this.elBox.width,
		D = this.elBox.height,
		E = this["handlerSize"],
		I = this.vertical ? D - this["handlerSize"] : B - this["handlerSize"],
		A = this.pane1.minSize,
		F = this.pane1.maxSize,
		$ = this.pane2.minSize,
		G = this.pane2.maxSize;
        if (this.vertical == true) {
            var _ = C.now[1] - C.init[1],
			H = this.handlerBox.y + _;
            if (H - this.elBox.y > F) H = this.elBox.y + F;
            if (H + this.handlerBox.height < this.elBox.bottom - G) H = this.elBox.bottom - G - this.handlerBox.height;
            if (H - this.elBox.y < A) H = this.elBox.y + A;
            if (H + this.handlerBox.height > this.elBox.bottom - $) H = this.elBox.bottom - $ - this.handlerBox.height;
            mini.setY(this.YL5C, H)
        } else {
            var J = C.now[0] - C.init[0],
			K = this.handlerBox.x + J;
            if (K - this.elBox.x > F) K = this.elBox.x + F;
            if (K + this.handlerBox.width < this.elBox.right - G) K = this.elBox.right - G - this.handlerBox.width;
            if (K - this.elBox.x < A) K = this.elBox.x + A;
            if (K + this.handlerBox.width > this.elBox.right - $) K = this.elBox.right - $ - this.handlerBox.width;
            mini.setX(this.YL5C, K)
        }
    },
    ZYd: function (_) {
        var $ = this.elBox.width,
		B = this.elBox.height,
		C = this["handlerSize"],
		D = parseFloat(this.pane1.size),
		E = parseFloat(this.pane2.size),
		I = isNaN(D),
		N = isNaN(E),
		J = !isNaN(D) && this.pane1.size.indexOf("%") != -1,
		M = !isNaN(E) && this.pane2.size.indexOf("%") != -1,
		G = !I && !J,
		K = !N && !M,
		L = this.vertical ? B - this["handlerSize"] : $ - this["handlerSize"],
		A = EcG(this.YL5C),
		H = A.x - this.elBox.x,
		F = L - H;
        if (this.vertical) {
            H = A.y - this.elBox.y;
            F = L - H
        }
        if (I || N) {
            if (I && N) {
                D = parseFloat(H / L * 100).toFixed(1);
                this.pane1.size = D + "%"
            } else if (G) {
                D = H;
                this.pane1.size = D
            } else if (J) {
                D = parseFloat(H / L * 100).toFixed(1);
                this.pane1.size = D + "%"
            } else if (K) {
                E = F;
                this.pane2.size = E
            } else if (M) {
                E = parseFloat(F / L * 100).toFixed(1);
                this.pane2.size = E + "%"
            }
        } else if (J && K) this.pane2.size = F;
        else if (G && M) this.pane1.size = H;
        else {
            this.pane1.size = parseFloat(H / L * 100).toFixed(1);
            this.pane2.size = 100 - this.pane1.size
        }
        jQuery(this.YL5C).remove();
        jQuery(this.SinP).remove();
        this.SinP = null;
        this.YL5C = null;
        this.elBox = this.handlerBox = null;
        this["doLayout"]()
    },
    getAttrs: function (B) {
        var G = mini.Splitter["superclass"]["getAttrs"]["call"](this, B);
        mini["_ParseBool"](B, G, ["allowResize", "vertical", "showHandleButton"]);
        mini["_ParseInt"](B, G, ["handlerSize"]);
        var A = [],
		F = mini["getChildNodes"](B);
        for (var _ = 0, E = 2; _ < E; _++) {
            var C = F[_],
			D = jQuery(C),
			$ = {};
            A.push($);
            if (!C) continue;
            $.style = C.style.cssText;
            mini["_ParseString"](C, $, ["cls", "size"]);
            mini["_ParseBool"](C, $, ["visible", "expanded", "showCollapseButton"]);
            mini["_ParseInt"](C, $, ["minSize", "maxSize", "handlerSize"]);
            $.bodyParent = C
        }
        G.panes = A;
        return G
    }
});
HbyG(mini.Splitter, "splitter");
mini.Layout = function () {
    this.regions = [];
    this.regionMap = {};
    mini.Layout["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Layout, mini.Control, {
    regions: [],
    splitSize: 6,
    collapseWidth: 28,
    collapseHeight: 25,
    regionWidth: 150,
    regionHeight: 80,
    regionMinWidth: 50,
    regionMinHeight: 25,
    regionMaxWidth: 2000,
    regionMaxHeight: 2000,
    uiCls: "mini-layout",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-layout";
        this.el.innerHTML = "<div class=\"mini-layout-border\"></div>";
        this._firstChild = this.el.firstChild;
        this["doUpdate"]()
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "click", this.YY, this);
            VNV(this.el, "mousedown", this.UNAE, this);
            VNV(this.el, "mouseover", this.Vv5, this);
            VNV(this.el, "mouseout", this.Gj$, this);
            VNV(document, "mousedown", this.La2, this)
        },
		this)
    },
    getRegionEl: function ($) {
        var $ = this["getRegion"]($);
        if (!$) return null;
        return $._el
    },
    getRegionHeaderEl: function ($) {
        var $ = this["getRegion"]($);
        if (!$) return null;
        return $._header
    },
    getRegionBodyEl: function ($) {
        var $ = this["getRegion"]($);
        if (!$) return null;
        return $._body
    },
    getRegionSplitEl: function ($) {
        var $ = this["getRegion"]($);
        if (!$) return null;
        return $._split
    },
    getRegionProxyEl: function ($) {
        var $ = this["getRegion"]($);
        if (!$) return null;
        return $._proxy
    },
    getRegionBox: function (_) {
        var $ = this["getRegionEl"](_);
        if ($) return EcG($);
        return null
    },
    getRegion: function ($) {
        if (typeof $ == "string") return this.regionMap[$];
        return $
    },
    LjK: function (_, B) {
        var D = _.buttons;
        for (var $ = 0, A = D.length; $ < A; $++) {
            var C = D[$];
            if (C.name == B) return C
        }
    },
    KXq: function (_) {
        var $ = mini.copyTo({
            region: "",
            title: "",
            iconCls: "",
            iconStyle: "",
            showCloseButton: false,
            showCollapseButton: true,
            buttons: [{
                name: "close",
                cls: "mini-tools-close",
                html: "",
                visible: false
            },
			{
			    name: "collapse",
			    cls: "mini-tools-collapse",
			    html: "",
			    visible: true
			}],
            showSplit: true,
            showHeader: true,
            splitSize: this.splitSize,
            collapseSize: this.collapseWidth,
            width: this.regionWidth,
            height: this.regionHeight,
            minWidth: this.regionMinWidth,
            minHeight: this.regionMinHeight,
            maxWidth: this.regionMaxWidth,
            maxHeight: this.regionMaxHeight,
            allowResize: true,
            cls: "",
            style: "",
            headerCls: "",
            headerStyle: "",
            bodyCls: "",
            bodyStyle: "",
            visible: true,
            expanded: true
        },
		_);
        return $
    },
    QFj4: function ($) {
        var $ = this["getRegion"]($);
        if (!$) return;
        mini.append(this._firstChild, "<div id=\"" + $.region + "\" class=\"mini-layout-region\"><div class=\"mini-layout-region-header\" style=\"" + $.headerStyle + "\"></div><div class=\"mini-layout-region-body\" style=\"" + $.bodyStyle + "\"></div></div>");
        $._el = this._firstChild.lastChild;
        $._header = $._el.firstChild;
        $._body = $._el.lastChild;
        if ($.cls) RE($._el, $.cls);
        if ($.style) BIT($._el, $.style);
        RE($._el, "mini-layout-region-" + $.region);
        if ($.region != "center") {
            mini.append(this._firstChild, "<div uid=\"" + this.uid + "\" id=\"" + $.region + "\" class=\"mini-layout-split\"></div>");
            $._split = this._firstChild.lastChild;
            RE($._split, "mini-layout-split-" + $.region)
        }
        if ($.region != "center") {
            mini.append(this._firstChild, "<div id=\"" + $.region + "\" class=\"mini-layout-proxy\"></div>");
            $._proxy = this._firstChild.lastChild;
            RE($._proxy, "mini-layout-proxy-" + $.region)
        }
    },
    setRegions: function (A) {
        if (!mini.isArray(A)) return;
        for (var $ = 0, _ = A.length; $ < _; $++) this.addRegion(A[$])
    },
    addRegion: function (D, $) {
        var G = D;
        D = this.KXq(D);
        if (!D.region) D.region = "center";
        D.region = D.region.toLowerCase();
        if (D.region == "center" && G && !G.showHeader) D.showHeader = false;
        if (D.region == "north" || D.region == "south") if (!G.collapseSize) D.collapseSize = this.collapseHeight;
        this.OMW(D);
        if (typeof $ != "number") $ = this.regions.length;
        var A = this.regionMap[D.region];
        if (A) return;
        this.regions.insert($, D);
        this.regionMap[D.region] = D;
        this.QFj4(D);
        var B = this.getRegionBodyEl(D),
		C = D.body;
        delete D.body;
        if (C) {
            if (!mini.isArray(C)) C = [C];
            for (var _ = 0, F = C.length; _ < F; _++) mini.append(B, C[_])
        }
        if (D.bodyParent) {
            var E = D.bodyParent;
            while (E.firstChild) B.appendChild(E.firstChild)
        }
        delete D.bodyParent;
        this["doUpdate"]()
    },
    removeRegion: function ($) {
        var $ = this["getRegion"]($);
        if (!$) return;
        this.regions.remove($);
        delete this.regionMap[$.region];
        jQuery($._el).remove();
        jQuery($._split).remove();
        jQuery($._proxy).remove();
        this["doUpdate"]()
    },
    moveRegion: function (A, $) {
        var A = this["getRegion"](A);
        if (!A) return;
        var _ = this.regions[$];
        if (!_ || _ == A) return;
        this.regions.remove(A);
        var $ = this.region.indexOf(_);
        this.regions.insert($, A);
        this["doUpdate"]()
    },
    OMW: function ($) {
        var _ = this.LjK($, "close");
        _.visible = $["showCloseButton"];
        _ = this.LjK($, "collapse");
        _.visible = $["showCollapseButton"];
        if ($.width < $.minWidth) $.width = mini.minWidth;
        if ($.width > $.maxWidth) $.width = mini.maxWidth;
        if ($.height < $.minHeight) $.height = mini.minHeight;
        if ($.height > $.maxHeight) $.height = mini.maxHeight
    },
    updateRegion: function ($, _) {
        $ = this["getRegion"]($);
        if (!$) return;
        if (_) delete _.region;
        mini.copyTo($, _);
        this.OMW($);
        this["doUpdate"]()
    },
    expandRegion: function ($) {
        $ = this["getRegion"]($);
        if (!$) return;
        $.expanded = true;
        this["doUpdate"]()
    },
    collapseRegion: function ($) {
        $ = this["getRegion"]($);
        if (!$) return;
        $.expanded = false;
        this["doUpdate"]()
    },
    toggleRegion: function ($) {
        $ = this["getRegion"]($);
        if (!$) return;
        if ($.expanded) this.collapseRegion($);
        else this.expandRegion($)
    },
    showRegion: function ($) {
        $ = this["getRegion"]($);
        if (!$) return;
        $.visible = true;
        this["doUpdate"]()
    },
    hideRegion: function ($) {
        $ = this["getRegion"]($);
        if (!$) return;
        $.visible = false;
        this["doUpdate"]()
    },
    isExpandRegion: function ($) {
        $ = this["getRegion"]($);
        if (!$) return null;
        return this.region.expanded
    },
    isVisibleRegion: function ($) {
        $ = this["getRegion"]($);
        if (!$) return null;
        return this.region.visible
    },
    Fcj: function ($) {
        $ = this["getRegion"]($);
        var _ = {
            region: $,
            cancel: false
        };
        if ($.expanded) {
            this.fire("BeforeCollapse", _);
            if (_.cancel == false) this.collapseRegion($)
        } else {
            this.fire("BeforeExpand", _);
            if (_.cancel == false) this.expandRegion($)
        }
    },
    YjcB: function (_) {
        var $ = MRj9(_.target, "mini-layout-proxy");
        return $
    },
    Fh5: function (_) {
        var $ = MRj9(_.target, "mini-layout-region");
        return $
    },
    YY: function (D) {
        if (this._g) return;
        var A = this.YjcB(D);
        if (A) {
            var _ = A.id,
			C = MRj9(D.target, "mini-tools-collapse");
            if (C) this.Fcj(_);
            else this.NIi(_)
        }
        var B = this.Fh5(D);
        if (B && MRj9(D.target, "mini-layout-region-header")) {
            _ = B.id,
			C = MRj9(D.target, "mini-tools-collapse");
            if (C) this.Fcj(_);
            var $ = MRj9(D.target, "mini-tools-close");
            if ($) this.updateRegion(_, {
                visible: false
            })
        }
    },
    QN: function (_, A, $) {
        this.fire("buttonclick", {
            htmlEvent: $,
            region: _,
            button: A,
            index: this.buttons.indexOf(A),
            name: A.name
        })
    },
    G$q: function (_, A, $) {
        this.fire("buttonmousedown", {
            htmlEvent: $,
            region: _,
            button: A,
            index: this.buttons.indexOf(A),
            name: A.name
        })
    },
    hoverProxyEl: null,
    Vv5: function (_) {
        var $ = this.YjcB(_);
        if ($) {
            RE($, "mini-layout-proxy-hover");
            this.hoverProxyEl = $
        }
    },
    Gj$: function ($) {
        if (this.hoverProxyEl) WeL(this.hoverProxyEl, "mini-layout-proxy-hover");
        this.hoverProxyEl = null
    },
    onButtonClick: function (_, $) {
        this.on("buttonclick", _, $)
    },
    onButtonMouseDown: function (_, $) {
        this.on("buttonmousedown", _, $)
    }
});
mini.copyTo(mini.Layout.prototype, {
    F9R: function (_, A) {
        var C = "<div class=\"mini-tools\">";
        if (A) C += "<span class=\"mini-tools-collapse\"></span>";
        else for (var $ = _.buttons.length - 1; $ >= 0; $--) {
            var B = _.buttons[$];
            C += "<span class=\"" + B.cls + "\" style=\"";
            C += B.style + ";" + (B.visible ? "" : "display:none;") + "\">" + B.html + "</span>"
        }
        C += "</div>";
        C += "<div class=\"mini-layout-region-icon " + _.iconCls + "\" style=\"" + _["iconStyle"] + ";" + ((_["iconStyle"] || _.iconCls) ? "" : "display:none;") + "\"></div>";
        C += "<div class=\"mini-layout-region-title\">" + _.title + "</div>";
        return C
    },
    doUpdate: function () {
        for (var $ = 0, E = this.regions.length; $ < E; $++) {
            var B = this.regions[$],
			_ = B.region,
			A = B._el,
			D = B._split,
			C = B._proxy;
            B._header.style.display = B.showHeader ? "" : "none";
            B._header.innerHTML = this.F9R(B);
            if (B._proxy) B._proxy.innerHTML = this.F9R(B, true);
            if (D) {
                WeL(D, "mini-layout-split-nodrag");
                if (B.expanded == false || !B["allowResize"]) RE(D, "mini-layout-split-nodrag")
            }
        }
        this["doLayout"]()
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        if (this._g) return;
        var C = Nf(this.el, true),
		_ = L_h(this.el, true),
		D = {
		    x: 0,
		    y: 0,
		    width: _,
		    height: C
		},
		I = this.regions.clone(),
		P = this["getRegion"]("center");
        I.remove(P);
        if (P) I.push(P);
        for (var K = 0, H = I.length; K < H; K++) {
            var E = I[K];
            E._Expanded = false;
            WeL(E._el, "mini-layout-popup");
            var A = E.region,
			L = E._el,
			F = E._split,
			G = E._proxy;
            if (E.visible == false) {
                L.style.display = "none";
                if (A != "center") F.style.display = G.style.display = "none";
                continue
            }
            L.style.display = "";
            if (A != "center") F.style.display = G.style.display = "";
            var R = D.x,
			O = D.y,
			_ = D.width,
			C = D.height,
			B = E.width,
			J = E.height;
            if (!E.expanded) if (A == "west" || A == "east") {
                B = E.collapseSize;
                SI9N(L, E.width)
            } else if (A == "north" || A == "south") {
                J = E.collapseSize;
                Quj1(L, E.height)
            }
            switch (A) {
                case "north":
                    C = J;
                    D.y += J;
                    D.height -= J;
                    break;
                case "south":
                    C = J;
                    O = D.y + D.height - J;
                    D.height -= J;
                    break;
                case "west":
                    _ = B;
                    D.x += B;
                    D.width -= B;
                    break;
                case "east":
                    _ = B;
                    R = D.x + D.width - B;
                    D.width -= B;
                    break;
                case "center":
                    break;
                default:
                    continue
            }
            if (_ < 0) _ = 0;
            if (C < 0) C = 0;
            if (A == "west" || A == "east") Quj1(L, C);
            if (A == "north" || A == "south") SI9N(L, _);
            var N = "left:" + R + "px;top:" + O + "px;",
			$ = L;
            if (!E.expanded) {
                $ = G;
                L.style.top = "-100px";
                L.style.left = "-1500px"
            } else if (G) {
                G.style.left = "-1500px";
                G.style.top = "-100px"
            }
            $.style.left = R + "px";
            $.style.top = O + "px";
            SI9N($, _);
            Quj1($, C);
            var M = jQuery(E._el).height(),
			Q = E.showHeader ? jQuery(E._header).outerHeight() : 0;
            Quj1(E._body, M - Q);
            if (A == "center") continue;
            B = J = E.splitSize;
            R = D.x,
			O = D.y,
			_ = D.width,
			C = D.height;
            switch (A) {
                case "north":
                    C = J;
                    D.y += J;
                    D.height -= J;
                    break;
                case "south":
                    C = J;
                    O = D.y + D.height - J;
                    D.height -= J;
                    break;
                case "west":
                    _ = B;
                    D.x += B;
                    D.width -= B;
                    break;
                case "east":
                    _ = B;
                    R = D.x + D.width - B;
                    D.width -= B;
                    break;
                case "center":
                    break
            }
            if (_ < 0) _ = 0;
            if (C < 0) C = 0;
            F.style.left = R + "px";
            F.style.top = O + "px";
            SI9N(F, _);
            Quj1(F, C);
            F.style.display = E.showSplit ? "block" : "none"
        }
        mini.layout(this._firstChild)
    },
    UNAE: function (B) {
        if (this._g) return;
        if (MRj9(B.target, "mini-layout-split")) {
            var A = jQuery(B.target).attr("uid");
            if (A != this.uid) return;
            var _ = this["getRegion"](B.target.id);
            if (_.expanded == false || !_["allowResize"]) return;
            this.dragRegion = _;
            var $ = this._Fc();
            $.start(B)
        }
    },
    _Fc: function () {
        if (!this.drag) this.drag = new mini.Drag({
            capture: true,
            onStart: mini.createDelegate(this.J_sA, this),
            onMove: mini.createDelegate(this.KsZ, this),
            onStop: mini.createDelegate(this.ZYd, this)
        });
        return this.drag
    },
    J_sA: function ($) {
        this.SinP = mini.append(document.body, "<div class=\"mini-resizer-mask\"></div>");
        this.YL5C = mini.append(document.body, "<div class=\"mini-proxy\"></div>");
        this.YL5C.style.cursor = "n-resize";
        if (this.dragRegion.region == "west" || this.dragRegion.region == "east") this.YL5C.style.cursor = "w-resize";
        this.splitBox = EcG(this.dragRegion._split);
        Ggi(this.YL5C, this.splitBox);
        this.elBox = EcG(this.el, true)
    },
    KsZ: function (C) {
        var I = C.now[0] - C.init[0],
		V = this.splitBox.x + I,
		A = C.now[1] - C.init[1],
		U = this.splitBox.y + A,
		K = V + this.splitBox.width,
		T = U + this.splitBox.height,
		G = this["getRegion"]("west"),
		L = this["getRegion"]("east"),
		F = this["getRegion"]("north"),
		D = this["getRegion"]("south"),
		H = this["getRegion"]("center"),
		O = G && G.visible ? G.width : 0,
		Q = L && L.visible ? L.width : 0,
		R = F && F.visible ? F.height : 0,
		J = D && D.visible ? D.height : 0,
		P = G && G.showSplit ? L_h(G._split) : 0,
		$ = L && L.showSplit ? L_h(L._split) : 0,
		B = F && F.showSplit ? Nf(F._split) : 0,
		S = D && D.showSplit ? Nf(D._split) : 0,
		E = this.dragRegion,
		N = E.region;
        if (N == "west") {
            var M = this.elBox.width - Q - $ - P - H.minWidth;
            if (V - this.elBox.x > M) V = M + this.elBox.x;
            if (V - this.elBox.x < E.minWidth) V = E.minWidth + this.elBox.x;
            if (V - this.elBox.x > E.maxWidth) V = E.maxWidth + this.elBox.x;
            mini.setX(this.YL5C, V)
        } else if (N == "east") {
            M = this.elBox.width - O - P - $ - H.minWidth;
            if (this.elBox.right - (V + this.splitBox.width) > M) V = this.elBox.right - M - this.splitBox.width;
            if (this.elBox.right - (V + this.splitBox.width) < E.minWidth) V = this.elBox.right - E.minWidth - this.splitBox.width;
            if (this.elBox.right - (V + this.splitBox.width) > E.maxWidth) V = this.elBox.right - E.maxWidth - this.splitBox.width;
            mini.setX(this.YL5C, V)
        } else if (N == "north") {
            var _ = this.elBox.height - J - S - B - H.minHeight;
            if (U - this.elBox.y > _) U = _ + this.elBox.y;
            if (U - this.elBox.y < E.minHeight) U = E.minHeight + this.elBox.y;
            if (U - this.elBox.y > E.maxHeight) U = E.maxHeight + this.elBox.y;
            mini.setY(this.YL5C, U)
        } else if (N == "south") {
            _ = this.elBox.height - R - B - S - H.minHeight;
            if (this.elBox.bottom - (U + this.splitBox.height) > _) U = this.elBox.bottom - _ - this.splitBox.height;
            if (this.elBox.bottom - (U + this.splitBox.height) < E.minHeight) U = this.elBox.bottom - E.minHeight - this.splitBox.height;
            if (this.elBox.bottom - (U + this.splitBox.height) > E.maxHeight) U = this.elBox.bottom - E.maxHeight - this.splitBox.height;
            mini.setY(this.YL5C, U)
        }
    },
    ZYd: function (B) {
        var C = EcG(this.YL5C),
		D = this.dragRegion,
		A = D.region;
        if (A == "west") {
            var $ = C.x - this.elBox.x;
            this.updateRegion(D, {
                width: $
            })
        } else if (A == "east") {
            $ = this.elBox.right - C.right;
            this.updateRegion(D, {
                width: $
            })
        } else if (A == "north") {
            var _ = C.y - this.elBox.y;
            this.updateRegion(D, {
                height: _
            })
        } else if (A == "south") {
            _ = this.elBox.bottom - C.bottom;
            this.updateRegion(D, {
                height: _
            })
        }
        jQuery(this.YL5C).remove();
        this.YL5C = null;
        this.elBox = this.handlerBox = null;
        jQuery(this.SinP).remove();
        this.SinP = null
    },
    NIi: function ($) {
        $ = this["getRegion"]($);
        if ($._Expanded === true) this.I1M($);
        else this.IJ7$($)
    },
    IJ7$: function (D) {
        if (this._g) return;
        this["doLayout"]();
        var A = D.region,
		H = D._el;
        D._Expanded = true;
        RE(H, "mini-layout-popup");
        var E = EcG(D._proxy),
		B = EcG(D._el),
		F = {};
        if (A == "east") {
            var K = E.x,
			J = E.y,
			C = E.height;
            Quj1(H, C);
            mini["setXY"](H, K, J);
            var I = parseInt(H.style.left);
            F = {
                left: I - B.width
            }
        } else if (A == "west") {
            K = E.right - B.width,
			J = E.y,
			C = E.height;
            Quj1(H, C);
            mini["setXY"](H, K, J);
            I = parseInt(H.style.left);
            F = {
                left: I + B.width
            }
        } else if (A == "north") {
            var K = E.x,
			J = E.bottom - B.height,
			_ = E.width;
            SI9N(H, _);
            mini["setXY"](H, K, J);
            var $ = parseInt(H.style.top);
            F = {
                top: $ + B.height
            }
        } else if (A == "south") {
            K = E.x,
			J = E.y,
			_ = E.width;
            SI9N(H, _);
            mini["setXY"](H, K, J);
            $ = parseInt(H.style.top);
            F = {
                top: $ - B.height
            }
        }
        RE(D._proxy, "mini-layout-maxZIndex");
        this._g = true;
        var G = this,
		L = jQuery(H);
        L.animate(F, 250,
		function () {
		    WeL(D._proxy, "mini-layout-maxZIndex");
		    G._g = false
		})
    },
    I1M: function (F) {
        if (this._g) return;
        F._Expanded = false;
        var B = F.region,
		E = F._el,
		D = EcG(E),
		_ = {};
        if (B == "east") {
            var C = parseInt(E.style.left);
            _ = {
                left: C + D.width
            }
        } else if (B == "west") {
            C = parseInt(E.style.left);
            _ = {
                left: C - D.width
            }
        } else if (B == "north") {
            var $ = parseInt(E.style.top);
            _ = {
                top: $ - D.height
            }
        } else if (B == "south") {
            $ = parseInt(E.style.top);
            _ = {
                top: $ + D.height
            }
        }
        RE(F._proxy, "mini-layout-maxZIndex");
        this._g = true;
        var A = this,
		G = jQuery(E);
        G.animate(_, 250,
		function () {
		    WeL(F._proxy, "mini-layout-maxZIndex");
		    A._g = false;
		    A["doLayout"]()
		})
    },
    La2: function (B) {
        if (this._g) return;
        for (var $ = 0, A = this.regions.length; $ < A; $++) {
            var _ = this.regions[$];
            if (!_._Expanded) continue;
            if (Yma(_._el, B.target) || Yma(_._proxy, B.target));
            else this.I1M(_)
        }
    },
    getAttrs: function (A) {
        var H = mini.Layout["superclass"]["getAttrs"]["call"](this, A),
		G = jQuery(A),
		E = parseInt(G.attr("splitSize"));
        if (!isNaN(E)) H.splitSize = E;
        var F = [],
		D = mini["getChildNodes"](A);
        for (var _ = 0, C = D.length; _ < C; _++) {
            var B = D[_],
			$ = {};
            F.push($);
            $.cls = B.className;
            $.style = B.style.cssText;
            mini["_ParseString"](B, $, ["region", "title", "iconCls", "iconStyle", "cls", "headerCls", "headerStyle", "bodyCls", "bodyStyle"]);
            mini["_ParseBool"](B, $, ["allowResize", "visible", "showCloseButton", "showCollapseButton", "showSplit", "showHeader", "expanded"]);
            mini["_ParseInt"](B, $, ["splitSize", "collapseSize", "width", "height", "minWidth", "minHeight", "maxWidth", "maxHeight"]);
            $.bodyParent = B
        }
        H.regions = F;
        return H
    }
});
HbyG(mini.Layout, "layout");
mini.Box = function () {
    mini.Box["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Box, mini.Control, {
    style: "",
    borderStyle: "",
    bodyStyle: "",
    uiCls: "mini-box",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-box";
        this.el.innerHTML = "<div class=\"mini-box-border\"></div>";
        this.$kJ = this._firstChild = this.el.firstChild
    },
    _initEvents: function () { },
    doLayout: function () {
        if (!this.canLayout()) return;
        var C = this["isAutoHeight"](),
		E = this["isAutoWidth"](),
		B = M32(this.$kJ),
		D = OFY(this.$kJ);
        if (!C) {
            var A = this["getHeight"](true);
            if (jQuery.boxModel) A = A - B.top - B.bottom;
            A = A - D.top - D.bottom;
            if (A < 0) A = 0;
            this.$kJ.style.height = A + "px"
        } else this.$kJ.style.height = "";
        var $ = this["getWidth"](true),
		_ = $;
        $ = $ - D.left - D.right;
        if (jQuery.boxModel) $ = $ - B.left - B.right;
        if ($ < 0) $ = 0;
        this.$kJ.style.width = $ + "px";
        mini.layout(this._firstChild)
    },
    setBody: function (_) {
        if (!_) return;
        if (!mini.isArray(_)) _ = [_];
        for (var $ = 0, A = _.length; $ < A; $++) mini.append(this.$kJ, _[$]);
        mini.parse(this.$kJ);
        this["doLayout"]()
    },
    set_bodyParent: function ($) {
        if (!$) return;
        var _ = this.$kJ,
		A = $;
        while (A.firstChild) _.appendChild(A.firstChild);
        this["doLayout"]()
    },
    setBodyStyle: function ($) {
        BIT(this.$kJ, $);
        this["doLayout"]()
    },
    getAttrs: function ($) {
        var _ = mini.Box["superclass"]["getAttrs"]["call"](this, $);
        _._bodyParent = $;
        mini["_ParseString"]($, _, ["bodyStyle"]);
        return _
    }
});
HbyG(mini.Box, "box");
mini.Include = function () {
    mini.Include["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Include, mini.Control, {
    url: "",
    uiCls: "mini-include",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-include"
    },
    _initEvents: function () { },
    doLayout: function () {
        if (!this.canLayout()) return;
        var A = this.el.childNodes;
        if (A) for (var $ = 0, B = A.length; $ < B; $++) {
            var _ = A[$];
            mini.layout(_)
        }
    },
    setUrl: function ($) {
        this.url = $;
        mini.update({
            url: this.url,
            el: this.el,
            async: this.async
        });
        this["doLayout"]()
    },
    getUrl: function ($) {
        return this.url
    },
    getAttrs: function ($) {
        var _ = mini.Include["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, _, ["url"]);
        return _
    }
});
HbyG(mini.Include, "include");
mini.Tabs = function () {
    this.K$H();
    mini.Tabs["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Tabs, mini.Control, {
    activeIndex: -1,
    tabAlign: "left",
    tabPosition: "top",
    showBody: true,
    nameField: "id",
    titleField: "title",
    urlField: "url",
    url: "",
    maskOnLoad: true,
    bodyStyle: "",
    MYe: "mini-tab-hover",
    KYL: "mini-tab-active",
    set: function ($) {
        if (typeof $ == "string") return this;
        var B = this.SX1V;
        this.SX1V = false;
        var _ = $.activeIndex;
        delete $.activeIndex;
        var A = $.url;
        delete $.url;
        mini.Tabs["superclass"].set["call"](this, $);
        if (A) this.setUrl(A);
        if (mini.isNumber(_)) this.setActiveIndex(_);
        this.SX1V = B;
        this["doLayout"]();
        return this
    },
    uiCls: "mini-tabs",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-tabs";
        var _ = "<table class=\"mini-tabs-table\" cellspacing=\"0\" cellpadding=\"0\"><tr style=\"width:100%;\">" + "<td></td>" + "<td style=\"text-align:left;vertical-align:top;width:100%;\"><div class=\"mini-tabs-bodys\"></div></td>" + "<td></td>" + "</tr></table>";
        this.el.innerHTML = _;
        this._2 = this.el.firstChild;
        var $ = this.el.getElementsByTagName("td");
        this.$k = $[0];
        this.Vl = $[1];
        this._h = $[2];
        this.$kJ = this.Vl.firstChild;
        this._firstChild = this.$kJ;
        this["doUpdate"]()
    },
    QTm: function () {
        WeL(this.$k, "mini-tabs-header");
        WeL(this._h, "mini-tabs-header");
        this.$k.innerHTML = "";
        this._h.innerHTML = "";
        mini.removeChilds(this.Vl, this.$kJ)
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "mousedown", this.UNAE, this);
            VNV(this.el, "click", this.YY, this);
            VNV(this.el, "mouseover", this.Vv5, this);
            VNV(this.el, "mouseout", this.Gj$, this)
        },
		this)
    },
    K$H: function () {
        this.tabs = []
    },
    IN4: 1,
    createTab: function (_) {
        var $ = mini.copyTo({
            _id: this.IN4++,
            name: "",
            title: "",
            newLine: false,
            iconCls: "",
            iconStyle: "",
            headerCls: "",
            headerStyle: "",
            bodyCls: "",
            bodyStyle: "",
            visible: true,
            enabled: true,
            showCloseButton: false,
            active: false,
            url: "",
            loaded: false,
            refreshOnClick: false
        },
		_);
        if (_) {
            _ = mini.copyTo(_, $);
            $ = _
        }
        return $
    },
    Sc: function () {
        var _ = mini.getData(this.url);
        if (!_) _ = [];
        for (var $ = 0, B = _.length; $ < B; $++) {
            var A = _[$];
            A.title = A[this.titleField];
            A.url = A[this.urlField];
            A.name = A[this.nameField]
        }
        this.setTabs(_);
        this.fire("load")
    },
    load: function ($) {
        if (typeof $ == "string") this.setUrl($);
        else this.setTabs($)
    },
    setUrl: function ($) {
        this.url = $;
        this.Sc()
    },
    getUrl: function () {
        return this.url
    },
    setNameField: function ($) {
        this.nameField = $
    },
    getNameField: function () {
        return this.nameField
    },
    setTitleField: function ($) {
        this["titleField"] = $
    },
    getTitleField: function () {
        return this["titleField"]
    },
    setUrlField: function ($) {
        this["urlField"] = $
    },
    getUrlField: function () {
        return this["urlField"]
    },
    setTabs: function (_) {
        if (!mini.isArray(_)) return;
        this.beginUpdate();
        this.removeAll();
        for (var $ = 0, A = _.length; $ < A; $++) this.addTab(_[$]);
        this.setActiveIndex(0);
        this.endUpdate()
    },
    getTabs: function () {
        return this.tabs
    },
    removeAll: function (A) {
        var E = this.getActiveTab();
        if (mini.isNull(A)) A = [];
        if (!mini.isArray(A)) A = [A];
        for (var $ = A.length - 1; $ >= 0; $--) {
            var B = this.getTab(A[$]);
            if (!B) A.removeAt($);
            else A[$] = B
        }
        var _ = this.tabs;
        for ($ = _.length - 1; $ >= 0; $--) {
            var D = _[$];
            if (A.indexOf(D) == -1) this.removeTab(D)
        }
        var C = A[0];
        if (E != this.getActiveTab()) if (C) this.activeTab(C)
    },
    addTab: function (C, $) {
        if (typeof C == "string") C = {
            title: C
        };
        C = this.createTab(C);
        if (!C.name) C.name = "";
        if (typeof $ != "number") $ = this.tabs.length;
        this.tabs.insert($, C);
        var F = this.Oun(C),
		G = "<div id=\"" + F + "\" class=\"mini-tabs-body " + C.bodyCls + "\" style=\"" + C.bodyStyle + ";display:none;\"></div>";
        mini.append(this.$kJ, G);
        var A = this.getTabBodyEl(C),
		B = C.body;
        delete C.body;
        if (B) {
            if (!mini.isArray(B)) B = [B];
            for (var _ = 0, E = B.length; _ < E; _++) mini.append(A, B[_])
        }
        if (C.bodyParent) {
            var D = C.bodyParent;
            while (D.firstChild) A.appendChild(D.firstChild)
        }
        delete C.bodyParent;
        this["doUpdate"]();
        return C
    },
    removeTab: function (C) {
        C = this.getTab(C);
        if (!C) return;
        var D = this.getActiveTab(),
		B = C == D,
		A = this.SrS(C);
        this.tabs.remove(C);
        this.YN(C);
        var _ = this.getTabBodyEl(C);
        if (_) this.$kJ.removeChild(_);
        if (A && B) {
            for (var $ = this.activeIndex; $ >= 0; $--) {
                var C = this.getTab($);
                if (C && C.enabled && C.visible) {
                    this.activeIndex = $;
                    break
                }
            }
            this["doUpdate"]();
            this.setActiveIndex(this.activeIndex);
            this.fire("activechanged")
        } else {
            this.activeIndex = this.tabs.indexOf(D);
            this["doUpdate"]()
        }
        return C
    },
    moveTab: function (A, $) {
        A = this.getTab(A);
        if (!A) return;
        var _ = this.tabs[$];
        if (!_ || _ == A) return;
        this.tabs.remove(A);
        var $ = this.tabs.indexOf(_);
        this.tabs.insert($, A);
        this["doUpdate"]()
    },
    updateTab: function ($, _) {
        $ = this.getTab($);
        if (!$) return;
        mini.copyTo($, _);
        this["doUpdate"]()
    },
    Gjy: function () {
        return this.$kJ
    },
    YN: function (C, A) {
        if (C.AeO && C.AeO.parentNode) {
            C.AeO.src = "";
            if (C.AeO._ondestroy) C.AeO._ondestroy();
            try {
                C.AeO.parentNode.removeChild(C.AeO);
                C.AeO["removeNode"](true)
            } catch (F) { }
        }
        C.AeO = null;
        C.loadedUrl = null;
        if (A === true) {
            var D = this.getTabBodyEl(C);
            if (D) {
                var B = mini["getChildNodes"](D, true);
                for (var _ = 0, E = B.length; _ < E; _++) {
                    var $ = B[_];
                    if ($ && $.parentNode) $.parentNode.removeChild($)
                }
            }
        }
    },
    Jo: 180,
    _cancelLoadTabs: function (B) {
        var _ = this.tabs;
        for (var $ = 0, C = _.length; $ < C; $++) {
            var A = _[$];
            if (A != B) if (A._loading && A.AeO) {
                A._loading = false;
                this.YN(A, true)
            }
        }
        this._loading = false;
        this.unmask()
    },
    _ah: function (A) {
        if (!A) return;
        var B = this.getTabBodyEl(A);
        if (!B) return;
        this._cancelLoadTabs();
        this.YN(A, true);
        this._loading = true;
        A._loading = true;
        this.unmask();
        if (this.maskOnLoad) this.loading();
        var C = new Date(),
		$ = this;
        $.isLoading = true;
        var _ = mini.createIFrame(A.url,
		function (_, D) {
		    try {
		        A.AeO.contentWindow.Owner = window;
		        A.AeO.contentWindow.CloseOwnerWindow = function (_) {
		            A.removeAction = _;
		            var B = true;
		            if (A.ondestroy) {
		                if (typeof A.ondestroy == "string") A.ondestroy = window[A.ondestroy];
		                if (A.ondestroy) B = A.ondestroy["call"](this, E)
		            }
		            if (B === false) return false;
		            setTimeout(function () {
		                $.removeTab(A)
		            },
					10)
		        }
		    } catch (E) { }
		    if (A._loading != true) return;
		    var B = (C - new Date()) + $.Jo;
		    A._loading = false;
		    A.loadedUrl = A.url;
		    if (B < 0) B = 0;
		    setTimeout(function () {
		        $.unmask();
		        $["doLayout"]();
		        $.isLoading = false
		    },
			B);
		    if (D) {
		        var E = {
		            sender: $,
		            tab: A,
		            index: $.tabs.indexOf(A),
		            name: A.name,
		            iframe: A.AeO
		        };
		        if (A.onload) {
		            if (typeof A.onload == "string") A.onload = window[A.onload];
		            if (A.onload) A.onload["call"]($, E)
		        }
		    }
		    $.fire("tabload", E)
		});
        setTimeout(function () {
            if (A.AeO == _) B.appendChild(_)
        },
		1);
        A.AeO = _
    },
    SrS: function ($) {
        var _ = {
            sender: this,
            tab: $,
            index: this.tabs.indexOf($),
            name: $.name,
            iframe: $.AeO,
            autoActive: true
        };
        this.fire("tabdestroy", _);
        return _.autoActive
    },
    loadTab: function (A, _, $, C) {
        if (!A) return;
        _ = this.getTab(_);
        if (!_) _ = this.getActiveTab();
        if (!_) return;
        _.url = A;
        delete _.loadedUrl;
        var B = this;
        clearTimeout(this._loadTabTimer);
        this._loadTabTimer = setTimeout(function () {
            B._ah(_)
        },
		1)
    },
    reloadTab: function ($) {
        $ = this.getTab($);
        if (!$) $ = this.getActiveTab();
        if (!$) return;
        this.loadTab($.url, $)
    },
    getTabRows: function () {
        var A = [],
		_ = [];
        for (var $ = 0, C = this.tabs.length; $ < C; $++) {
            var B = this.tabs[$];
            if ($ != 0 && B.newLine) {
                A.push(_);
                _ = []
            }
            _.push(B)
        }
        A.push(_);
        return A
    },
    doUpdate: function () {
        if (this.N2b === false) return;
        WeL(this.el, "mini-tabs-position-left");
        WeL(this.el, "mini-tabs-position-top");
        WeL(this.el, "mini-tabs-position-right");
        WeL(this.el, "mini-tabs-position-bottom");
        if (this["tabPosition"] == "bottom") {
            RE(this.el, "mini-tabs-position-bottom");
            this.YEL4()
        } else if (this["tabPosition"] == "right") {
            RE(this.el, "mini-tabs-position-right");
            this.DqT()
        } else if (this["tabPosition"] == "left") {
            RE(this.el, "mini-tabs-position-left");
            this.HW5()
        } else {
            RE(this.el, "mini-tabs-position-top");
            this.D_()
        }
        this["doLayout"]();
        this.setActiveIndex(this.activeIndex, false)
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        var R = this["isAutoHeight"]();
        C = this["getHeight"](true);
        w = this["getWidth"](true);
        var G = C,
		O = w;
        if (!R && this["showBody"]) {
            var Q = jQuery(this.VEE).outerHeight(),
			$ = jQuery(this.VEE).outerWidth();
            if (this["tabPosition"] == "top") Q = jQuery(this.VEE.parentNode).outerHeight();
            if (this["tabPosition"] == "left" || this["tabPosition"] == "right") w = w - $;
            else C = C - Q;
            if (jQuery.boxModel) {
                var D = M32(this.$kJ),
				S = Ly5(this.$kJ);
                C = C - D.top - D.bottom - S.top - S.bottom;
                w = w - D.left - D.right - S.left - S.right
            }
            margin = OFY(this.$kJ);
            C = C - margin.top - margin.bottom;
            w = w - margin.left - margin.right;
            if (C < 0) C = 0;
            if (w < 0) w = 0;
            this.$kJ.style.width = w + "px";
            this.$kJ.style.height = C + "px";
            if (this["tabPosition"] == "left" || this["tabPosition"] == "right") {
                var I = this.VEE.getElementsByTagName("tr")[0],
				E = I.childNodes,
				_ = E[0].getElementsByTagName("tr"),
				F = last = all = 0;
                for (var K = 0, H = _.length; K < H; K++) {
                    var I = _[K],
					N = jQuery(I).outerHeight();
                    all += N;
                    if (K == 0) F = N;
                    if (K == H - 1) last = N
                }
                switch (this["tabAlign"]) {
                    case "center":
                        var P = parseInt((G - (all - F - last)) / 2);
                        for (K = 0, H = E.length; K < H; K++) {
                            E[K].firstChild.style.height = G + "px";
                            var B = E[K].firstChild,
						_ = B.getElementsByTagName("tr"),
						L = _[0],
						U = _[_.length - 1];
                            L.style.height = P + "px";
                            U.style.height = P + "px"
                        }
                        break;
                    case "right":
                        for (K = 0, H = E.length; K < H; K++) {
                            var B = E[K].firstChild,
						_ = B.getElementsByTagName("tr"),
						I = _[0],
						T = G - (all - F);
                            if (T >= 0) I.style.height = T + "px"
                        }
                        break;
                    case "fit":
                        for (K = 0, H = E.length; K < H; K++) E[K].firstChild.style.height = G + "px";
                        break;
                    default:
                        for (K = 0, H = E.length; K < H; K++) {
                            B = E[K].firstChild,
						_ = B.getElementsByTagName("tr"),
						I = _[_.length - 1],
						T = G - (all - last);
                            if (T >= 0) I.style.height = T + "px"
                        }
                        break
                }
            }
        } else {
            this.$kJ.style.width = "auto";
            this.$kJ.style.height = "auto"
        }
        var A = this.getTabBodyEl(this.activeIndex);
        if (A) if (!R && this["showBody"]) {
            var C = Nf(this.$kJ, true);
            if (jQuery.boxModel) {
                D = M32(A),
				S = Ly5(A);
                C = C - D.top - D.bottom - S.top - S.bottom
            }
            A.style.height = C + "px"
        } else A.style.height = "auto";
        switch (this["tabPosition"]) {
            case "bottom":
                var M = this.VEE.childNodes;
                for (K = 0, H = M.length; K < H; K++) {
                    B = M[K];
                    WeL(B, "mini-tabs-header2");
                    if (H > 1 && K != 0) RE(B, "mini-tabs-header2")
                }
                break;
            case "left":
                E = this.VEE.firstChild.rows[0].cells;
                for (K = 0, H = E.length; K < H; K++) {
                    var J = E[K];
                    WeL(J, "mini-tabs-header2");
                    if (H > 1 && K == 0) RE(J, "mini-tabs-header2")
                }
                break;
            case "right":
                E = this.VEE.firstChild.rows[0].cells;
                for (K = 0, H = E.length; K < H; K++) {
                    J = E[K];
                    WeL(J, "mini-tabs-header2");
                    if (H > 1 && K != 0) RE(J, "mini-tabs-header2")
                }
                break;
            default:
                M = this.VEE.childNodes;
                for (K = 0, H = M.length; K < H; K++) {
                    B = M[K];
                    WeL(B, "mini-tabs-header2");
                    if (H > 1 && K == 0) RE(B, "mini-tabs-header2")
                }
                break
        }
        WeL(this.el, "mini-tabs-scroll");
        if (this["tabPosition"] == "top") {
            jQuery(this.VEE).width(O);
            if (this.VEE.offsetWidth < this.VEE.scrollWidth) {
                jQuery(this.VEE).width(O - 60);
                RE(this.el, "mini-tabs-scroll")
            }
            if (isIE && !jQuery.boxModel) this.$kT_.style.left = "-26px"
        }
        this.Ye();
        mini.layout(this.$kJ)
    },
    setTabAlign: function ($) {
        this["tabAlign"] = $;
        this["doUpdate"]()
    },
    setTabPosition: function ($) {
        this["tabPosition"] = $;
        this["doUpdate"]()
    },
    getTab: function ($) {
        if (typeof $ == "object") return $;
        if (typeof $ == "number") return this.tabs[$];
        else for (var _ = 0, B = this.tabs.length; _ < B; _++) {
            var A = this.tabs[_];
            if (A.name == $) return A
        }
    },
    getHeaderEl: function () {
        return this.VEE
    },
    getBodyEl: function () {
        return this.$kJ
    },
    getTabEl: function ($) {
        var C = this.getTab($);
        if (!C) return null;
        var E = this.SPM(C),
		B = this.el.getElementsByTagName("*");
        for (var _ = 0, D = B.length; _ < D; _++) {
            var A = B[_];
            if (A.id == E) return A
        }
        return null
    },
    getTabBodyEl: function ($) {
        var C = this.getTab($);
        if (!C) return null;
        var E = this.Oun(C),
		B = this.$kJ.childNodes;
        for (var _ = 0, D = B.length; _ < D; _++) {
            var A = B[_];
            if (A.id == E) return A
        }
        return null
    },
    getTabIFrameEl: function ($) {
        var _ = this.getTab($);
        if (!_) return null;
        return _.AeO
    },
    SPM: function ($) {
        return this.uid + "$" + $._id
    },
    Oun: function ($) {
        return this.uid + "$body$" + $._id
    },
    Ye: function () {
        if (this["tabPosition"] == "top") {
            WeL(this.$kT_, "mini-disabled");
            WeL(this.BP1p, "mini-disabled");
            if (this.VEE.scrollLeft == 0) RE(this.$kT_, "mini-disabled");
            var _ = this.getTabEl(this.tabs.length - 1);
            if (_) {
                var $ = EcG(_),
				A = EcG(this.VEE);
                if ($.right <= A.right) RE(this.BP1p, "mini-disabled")
            }
        }
    },
    setActiveIndex: function ($, I) {
        var M = this.getTab($),
		C = this.getTab(this.activeIndex),
		N = M != C,
		K = this.getTabBodyEl(this.activeIndex);
        if (K) K.style.display = "none";
        if (M) this.activeIndex = this.tabs.indexOf(M);
        else this.activeIndex = -1;
        K = this.getTabBodyEl(this.activeIndex);
        if (K) K.style.display = "";
        K = this.getTabEl(C);
        if (K) WeL(K, this.KYL);
        K = this.getTabEl(M);
        if (K) RE(K, this.KYL);
        if (K && N) {
            if (this["tabPosition"] == "bottom") {
                var A = MRj9(K, "mini-tabs-header");
                if (A) jQuery(this.VEE).prepend(A)
            } else if (this["tabPosition"] == "left") {
                var G = MRj9(K, "mini-tabs-header").parentNode;
                if (G) G.parentNode.appendChild(G)
            } else if (this["tabPosition"] == "right") {
                G = MRj9(K, "mini-tabs-header").parentNode;
                if (G) jQuery(G.parentNode).prepend(G)
            } else {
                A = MRj9(K, "mini-tabs-header");
                if (A) this.VEE.appendChild(A)
            }
            var B = this.VEE.scrollLeft;
            this["doLayout"]();
            var _ = this.getTabRows();
            if (_.length > 1);
            else {
                if (this["tabPosition"] == "top") {
                    this.VEE.scrollLeft = B;
                    var O = this.getTabEl(this.activeIndex);
                    if (O) {
                        var J = this,
						L = EcG(O),
						F = EcG(J.VEE);
                        if (L.x < F.x) J.VEE.scrollLeft -= (F.x - L.x);
                        else if (L.right > F.right) J.VEE.scrollLeft += (L.right - F.right)
                    }
                }
                this.Ye()
            }
            for (var H = 0, E = this.tabs.length; H < E; H++) {
                O = this.getTabEl(this.tabs[H]);
                if (O) WeL(O, this.MYe)
            }
        }
        var D = this;
        if (N) {
            var P = {
                tab: M,
                index: this.tabs.indexOf(M),
                name: M.name
            };
            setTimeout(function () {
                D.fire("ActiveChanged", P)
            },
			1)
        }
        this._cancelLoadTabs(M);
        if (I !== false) if (M && M.url && !M.loadedUrl) {
            D = this;
            D.loadTab(M.url, M)
        }
        if (D.canLayout()) {
            try {
                mini.layoutIFrames(D.el)
            } catch (P) { }
        }
    },
    getActiveIndex: function () {
        return this.activeIndex
    },
    activeTab: function ($) {
        this.setActiveIndex($)
    },
    getActiveTab: function () {
        return this.getTab(this.activeIndex)
    },
    getActiveIndex: function () {
        return this.activeIndex
    },
    Hor: function (_) {
        _ = this.getTab(_);
        if (!_) return;
        var $ = this.tabs.indexOf(_);
        if (this.activeIndex == $) return;
        var A = {
            tab: _,
            index: $,
            name: _.name,
            cancel: false
        };
        this.fire("BeforeActiveChanged", A);
        if (A.cancel == false) this.activeTab(_)
    },
    setShowBody: function ($) {
        if (this["showBody"] != $) {
            this["showBody"] = $;
            this["doLayout"]()
        }
    },
    getShowBody: function () {
        return this["showBody"]
    },
    setBodyStyle: function ($) {
        this.bodyStyle = $;
        BIT(this.$kJ, $);
        this["doLayout"]()
    },
    getBodyStyle: function () {
        return this.bodyStyle
    },
    setMaskOnLoad: function ($) {
        this.maskOnLoad = $
    },
    getMaskOnLoad: function () {
        return this.maskOnLoad
    },
    getTabByEvent: function ($) {
        return this.EGUR($)
    },
    EGUR: function (B) {
        var A = MRj9(B.target, "mini-tab");
        if (!A) return null;
        var _ = A.id.split("$");
        if (_[0] != this.uid) return null;
        var $ = parseInt(jQuery(A).attr("index"));
        return this.getTab($)
    },
    YY: function (A) {
        if (this.isLoading) return;
        var $ = this.EGUR(A);
        if (!$) return;
        if ($.enabled) {
            var _ = this;
            setTimeout(function () {
                if (MRj9(A.target, "mini-tab-close")) _.Wgh($, A);
                else {
                    var B = $.loadedUrl;
                    _.Hor($);
                    if ($["refreshOnClick"] && $.url == B) _.reloadTab($)
                }
            },
			10)
        }
    },
    hoverTab: null,
    Vv5: function (A) {
        var $ = this.EGUR(A);
        if ($ && $.enabled) {
            var _ = this.getTabEl($);
            RE(_, this.MYe);
            this.hoverTab = $
        }
    },
    Gj$: function (_) {
        if (this.hoverTab) {
            var $ = this.getTabEl(this.hoverTab);
            WeL($, this.MYe)
        }
        this.hoverTab = null
    },
    UNAE: function (B) {
        clearInterval(this.Qj2);
        if (this["tabPosition"] == "top") {
            var _ = this,
			A = 0,
			$ = 10;
            if (B.target == this.$kT_) this.Qj2 = setInterval(function () {
                _.VEE.scrollLeft -= $;
                A++;
                if (A > 5) $ = 18;
                if (A > 10) $ = 25;
                _.Ye()
            },
			25);
            else if (B.target == this.BP1p) this.Qj2 = setInterval(function () {
                _.VEE.scrollLeft += $;
                A++;
                if (A > 5) $ = 18;
                if (A > 10) $ = 25;
                _.Ye()
            },
			25);
            VNV(document, "mouseup", this.NkrJ, this)
        }
    },
    NkrJ: function ($) {
        clearInterval(this.Qj2);
        this.Qj2 = null;
        BP(document, "mouseup", this.NkrJ, this)
    },
    D_: function () {
        var L = this["tabPosition"] == "top",
		O = "";
        if (L) {
            O += "<div class=\"mini-tabs-scrollCt\">";
            O += "<a class=\"mini-tabs-leftButton\" href=\"javascript:void(0)\" hideFocus onclick=\"return false\"></a><a class=\"mini-tabs-rightButton\" href=\"javascript:void(0)\" hideFocus onclick=\"return false\"></a>"
        }
        O += "<div class=\"mini-tabs-headers\">";
        var B = this.getTabRows();
        for (var M = 0, A = B.length; M < A; M++) {
            var I = B[M],
			E = "";
            O += "<table class=\"mini-tabs-header\" cellspacing=\"0\" cellpadding=\"0\"><tr><td class=\"mini-tabs-space mini-tabs-firstSpace\"><div></div></td>";
            for (var J = 0, F = I.length; J < F; J++) {
                var N = I[J],
				G = this.SPM(N);
                if (!N.visible) continue;
                var $ = this.tabs.indexOf(N),
				E = N.headerCls || "";
                if (N.enabled == false) E += " mini-disabled";
                O += "<td id=\"" + G + "\" index=\"" + $ + "\"  class=\"mini-tab " + E + "\" style=\"" + N.headerStyle + "\">";
                if (N.iconCls || N["iconStyle"]) O += "<span class=\"mini-tab-icon " + N.iconCls + "\" style=\"" + N["iconStyle"] + "\"></span>";
                O += "<span class=\"mini-tab-text\">" + N.title + "</span>";
                if (N["showCloseButton"]) {
                    var _ = "";
                    if (N.enabled) _ = "onmouseover=\"RE(this,'mini-tab-close-hover')\" onmouseout=\"WeL(this,'mini-tab-close-hover')\"";
                    O += "<span class=\"mini-tab-close\" " + _ + "></span>"
                }
                O += "</td>";
                if (J != F - 1) O += "<td class=\"mini-tabs-space2\"><div></div></td>"
            }
            O += "<td class=\"mini-tabs-space mini-tabs-lastSpace\" ><div></div></td></tr></table>"
        }
        if (L) O += "</div>";
        O += "</div>";
        this.QTm();
        mini.prepend(this.Vl, O);
        var H = this.Vl;
        this.VEE = H.firstChild.lastChild;
        if (L) {
            this.$kT_ = this.VEE.parentNode.firstChild;
            this.BP1p = this.VEE.parentNode.childNodes[1]
        }
        switch (this["tabAlign"]) {
            case "center":
                var K = this.VEE.childNodes;
                for (J = 0, F = K.length; J < F; J++) {
                    var C = K[J],
				D = C.getElementsByTagName("td");
                    D[0].style.width = "50%";
                    D[D.length - 1].style.width = "50%"
                }
                break;
            case "right":
                K = this.VEE.childNodes;
                for (J = 0, F = K.length; J < F; J++) {
                    C = K[J],
				D = C.getElementsByTagName("td");
                    D[0].style.width = "100%"
                }
                break;
            case "fit":
                break;
            default:
                K = this.VEE.childNodes;
                for (J = 0, F = K.length; J < F; J++) {
                    C = K[J],
				D = C.getElementsByTagName("td");
                    D[D.length - 1].style.width = "100%"
                }
                break
        }
    },
    YEL4: function () {
        this.D_();
        var $ = this.Vl;
        mini.append($, $.firstChild);
        this.VEE = $.lastChild
    },
    HW5: function () {
        var J = "<table cellspacing=\"0\" cellpadding=\"0\"><tr>",
		B = this.getTabRows();
        for (var H = 0, A = B.length; H < A; H++) {
            var F = B[H],
			C = "";
            if (A > 1 && H != A - 1) C = "mini-tabs-header2";
            J += "<td class=\"" + C + "\"><table class=\"mini-tabs-header\" cellspacing=\"0\" cellpadding=\"0\">";
            J += "<tr ><td class=\"mini-tabs-space mini-tabs-firstSpace\" ><div></div></td></tr>";
            for (var G = 0, D = F.length; G < D; G++) {
                var I = F[G],
				E = this.SPM(I);
                if (!I.visible) continue;
                var $ = this.tabs.indexOf(I),
				C = I.headerCls || "";
                if (I.enabled == false) C += " mini-disabled";
                J += "<tr><td id=\"" + E + "\" index=\"" + $ + "\"  class=\"mini-tab " + C + "\" style=\"" + I.headerStyle + "\">";
                if (I.iconCls || I["iconStyle"]) J += "<span class=\"mini-tab-icon " + I.iconCls + "\" style=\"" + I["iconStyle"] + "\"></span>";
                J += "<span class=\"mini-tab-text\">" + I.title + "</span>";
                if (I["showCloseButton"]) {
                    var _ = "";
                    if (I.enabled) _ = "onmouseover=\"RE(this,'mini-tab-close-hover')\" onmouseout=\"WeL(this,'mini-tab-close-hover')\"";
                    J += "<span class=\"mini-tab-close\" " + _ + "></span>"
                }
                J += "</td></tr>";
                if (G != D - 1) J += "<tr><td class=\"mini-tabs-space2\"><div></div></td></tr>"
            }
            J += "<tr ><td class=\"mini-tabs-space mini-tabs-lastSpace\" ><div></div></td></tr>";
            J += "</table></td>"
        }
        J += "</tr ></table>";
        this.QTm();
        RE(this.$k, "mini-tabs-header");
        mini.append(this.$k, J);
        this.VEE = this.$k
    },
    DqT: function () {
        this.HW5();
        WeL(this.$k, "mini-tabs-header");
        WeL(this._h, "mini-tabs-header");
        mini.append(this._h, this.$k.firstChild);
        this.VEE = this._h
    },
    Wgh: function (_, $) {
        var C = {
            tab: _,
            index: this.tabs.indexOf(_),
            name: _.name.toLowerCase(),
            htmlEvent: $,
            cancel: false
        };
        this.fire("beforecloseclick", C);
        try {
            if (_.AeO && _.AeO.contentWindow) {
                var A = true;
                if (_.AeO.contentWindow.CloseWindow) A = _.AeO.contentWindow.CloseWindow("close");
                else if (_.AeO.contentWindow.CloseOwnerWindow) A = _.AeO.contentWindow.CloseOwnerWindow("close");
                if (A === false) C.cancel = true
            }
        } catch (B) { }
        if (C.cancel == true) return;
        _.removeAction = "close";
        this.removeTab(_);
        this.fire("closeclick", C)
    },
    onBeforeCloseClick: function (_, $) {
        this.on("beforecloseclick", _, $)
    },
    onCloseClick: function (_, $) {
        this.on("closeclick", _, $)
    },
    onActiveChanged: function (_, $) {
        this.on("activechanged", _, $)
    },
    getAttrs: function (B) {
        var F = mini.Tabs["superclass"]["getAttrs"]["call"](this, B);
        mini["_ParseString"](B, F, ["tabAlign", "tabPosition", "bodyStyle", "onactivechanged", "onbeforeactivechanged", "url", "ontabload", "ontabdestroy", "onbeforecloseclick", "oncloseclick", "titleField", "urlField", "nameField", "loadingMsg"]);
        mini["_ParseBool"](B, F, ["allowAnim", "showBody", "maskOnLoad"]);
        mini["_ParseInt"](B, F, ["activeIndex"]);
        var A = [],
		E = mini["getChildNodes"](B);
        for (var _ = 0, D = E.length; _ < D; _++) {
            var C = E[_],
			$ = {};
            A.push($);
            $.style = C.style.cssText;
            mini["_ParseString"](C, $, ["name", "title", "url", "cls", "iconCls", "iconStyle", "headerCls", "headerStyle", "bodyCls", "bodyStyle", "onload", "ondestroy"]);
            mini["_ParseBool"](C, $, ["newLine", "visible", "enabled", "showCloseButton", "refreshOnClick"]);
            $.bodyParent = C
        }
        F.tabs = A;
        return F
    }
});
HbyG(mini.Tabs, "tabs");
mini.Menu = function () {
    this.items = [];
    mini.Menu["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Menu, mini.Control);
mini.copyTo(mini.Menu.prototype, SEV5_prototype);
var SEV5_prototype_hide = SEV5_prototype.hide;
mini.copyTo(mini.Menu.prototype, {
    width: 140,
    vertical: true,
    allowSelectItem: false,
    Nab9: null,
    _Cm: "mini-menuitem-selected",
    textField: "text",
    resultAsTree: false,
    idField: "id",
    parentField: "pid",
    itemsField: "children",
    _clearBorder: false,
    showAction: "none",
    hideAction: "outerclick",
    getbyName: function (C) {
        for (var _ = 0, B = this.items.length; _ < B; _++) {
            var $ = this.items[_];
            if ($.name == C) return $;
            if ($.menu) {
                var A = $.menu.getbyName(C);
                if (A) return A
            }
        }
        return null
    },
    set: function ($) {
        if (typeof $ == "string") return this;
        var _ = $.url;
        delete $.url;
        mini.Menu["superclass"].set["call"](this, $);
        if (_) this.setUrl(_);
        return this
    },
    uiCls: "mini-menu",
    _create: function () {
        var _ = "<table class=\"mini-menu\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"text-align:left;vertical-align:top;padding:0;border:0;\"><div class=\"mini-menu-inner\"></div></td></tr></table>",
		$ = document.createElement("div");
        $.innerHTML = _;
        this.el = $.firstChild;
        this._contentEl = mini.byClass("mini-menu-inner", this.el);
        if (this.isVertical() == false) RE(this.el, "mini-menu-horizontal")
    },
    destroy: function ($) {
        this._popupEl = this.popupEl = null;
        this.owner = null;
        BP(document, "mousedown", this.LzKR, this);
        BP(window, "resize", this.CZh, this);
        mini.Menu["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(document, "mousedown", this.LzKR, this);
            VNV(this.el, "mouseover", this.Vv5, this);
            VNV(window, "resize", this.CZh, this);
            VNV(this.el, "contextmenu",
			function ($) {
			    $.preventDefault()
			},
			this)
        },
		this)
    },
    within: function (B) {
        if (Yma(this.el, B.target)) return true;
        for (var _ = 0, A = this.items.length; _ < A; _++) {
            var $ = this.items[_];
            if ($["within"](B)) return true
        }
        return false
    },
    Yz: function () {
        if (!this._clearEl) this._clearEl = mini.append(this._contentEl, "<div style=\"clear:both;\"></div>");
        return this._clearEl
    },
    setVertical: function ($) {
        this.vertical = $;
        if (!$) RE(this.el, "mini-menu-horizontal");
        else WeL(this.el, "mini-menu-horizontal");
        mini.append(this._contentEl, this.Yz())
    },
    getVertical: function () {
        return this.vertical
    },
    isVertical: function () {
        return this.vertical
    },
    show: function () {
        this["setVisible"](true)
    },
    hide: function () {
        this.hideItems();
        SEV5_prototype_hide["call"](this)
    },
    hideItems: function () {
        for (var $ = 0, A = this.items.length; $ < A; $++) {
            var _ = this.items[$];
            _.hideMenu()
        }
    },
    showItemMenu: function ($) {
        for (var _ = 0, B = this.items.length; _ < B; _++) {
            var A = this.items[_];
            if (A == $) A.showMenu();
            else A.hideMenu()
        }
    },
    hasShowItemMenu: function () {
        for (var $ = 0, A = this.items.length; $ < A; $++) {
            var _ = this.items[$];
            if (_ && _.menu && _.menu.isPopup) return true
        }
        return false
    },
    setItems: function (_) {
        if (!mini.isArray(_)) return;
        this.removeAll();
        for (var $ = 0, A = _.length; $ < A; $++) this.addItem(_[$])
    },
    getItems: function () {
        return this.items
    },
    addItem: function ($) {
        if ($ == "-" || $ == "|") {
            mini.append(this._contentEl, "<span class=\"mini-separator\"></span>");
            return
        }
        if (!mini.isControl($) && !mini.getClass($.type)) $.type = "menuitem";
        $ = mini.getAndCreate($);
        this.items.push($);
        this._contentEl.appendChild($.el);
        $.ownerMenu = this;
        mini.append(this._contentEl, this.Yz());
        this.fire("itemschanged")
    },
    removeItem: function ($) {
        $ = mini.get($);
        if (!$) return;
        this.items.remove($);
        this._contentEl.removeChild($.el);
        this.fire("itemschanged")
    },
    removeItemAt: function (_) {
        var $ = this.items[_];
        this.removeItem($)
    },
    removeAll: function () {
        var _ = this.items.clone();
        for (var $ = _.length - 1; $ >= 0; $--) this.removeItem(_[$]);
        this._contentEl.innerHTML = ""
    },
    getGroupItems: function (C) {
        if (!C) return [];
        var A = [];
        for (var _ = 0, B = this.items.length; _ < B; _++) {
            var $ = this.items[_];
            if ($["groupName"] == C) A.push($)
        }
        return A
    },
    getItem: function ($) {
        if (typeof $ == "number") return this.items[$];
        return $
    },
    setAllowSelectItem: function ($) {
        this.allowSelectItem = $
    },
    getAllowSelectItem: function () {
        return this.allowSelectItem
    },
    setSelectedItem: function ($) {
        $ = this["getItem"]($);
        this._OnItemSelect($)
    },
    getSelectedItem: function ($) {
        return this.Nab9
    },
    setTextField: function ($) {
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    setResultAsTree: function ($) {
        this["resultAsTree"] = $
    },
    getResultAsTree: function () {
        return this["resultAsTree"]
    },
    setIdField: function ($) {
        this["idField"] = $
    },
    getIdField: function () {
        return this["idField"]
    },
    setParentField: function ($) {
        this["parentField"] = $
    },
    getParentField: function () {
        return this["parentField"]
    },
    url: "",
    Sc: function () {
        var B = mini.getData(this.url);
        if (!B) B = [];
        if (this["resultAsTree"] == false) B = mini.arrayToTree(B, this.itemsField, this.idField, this["parentField"]);
        var _ = mini["treeToArray"](B, this.itemsField, this.idField, this["parentField"]);
        for (var A = 0, C = _.length; A < C; A++) {
            var $ = _[A];
            $.text = $[this.textField]
        }
        this.setItems(B);
        this.fire("load")
    },
    load: function ($) {
        if (typeof $ == "string") this.setUrl($);
        else this.setItems($)
    },
    setUrl: function ($) {
        this.url = $;
        this.Sc()
    },
    getUrl: function () {
        return this.url
    },
    _OnItemClick: function ($, _) {
        var A = {
            item: $,
            isLeaf: !$.menu,
            htmlEvent: _
        };
        if (this.isPopup) this.hide();
        else this.hideItems();
        if (this.allowSelectItem) this.setSelectedItem($);
        this.fire("itemclick", A);
        if (this.ownerItem);
    },
    _OnItemSelect: function ($) {
        if (this.Nab9) this.Nab9["removeCls"](this._Cm);
        this.Nab9 = $;
        if (this.Nab9) this.Nab9["addCls"](this._Cm);
        var _ = {
            item: this.Nab9
        };
        this.fire("itemselect", _)
    },
    onItemClick: function (_, $) {
        this.on("itemclick", _, $)
    },
    onItemSelect: function (_, $) {
        this.on("itemselect", _, $)
    },
    parseItems: function (G) {
        var C = [];
        for (var _ = 0, F = G.length; _ < F; _++) {
            var B = G[_];
            if (B.className == "separator") {
                C.add("-");
                continue
            }
            var E = mini["getChildNodes"](B),
			A = E[0],
			D = E[1],
			$ = new mini.MenuItem();
            if (!D) {
                mini.applyTo["call"]($, B);
                C.add($);
                continue
            }
            mini.applyTo["call"]($, A);
            $["render"](document.body);
            var H = new mini.Menu();
            mini.applyTo["call"](H, D);
            $.setMenu(H);
            H["render"](document.body);
            C.add($)
        }
        return C.clone()
    },
    getAttrs: function (_) {
        var E = mini.Menu["superclass"]["getAttrs"]["call"](this, _),
		D = jQuery(_);
        mini["_ParseString"](_, E, ["popupEl", "popupCls", "showAction", "hideAction", "hAlign", "vAlign", "modalStyle", "onbeforeopen", "open", "onbeforeclose", "onclose", "url", "onitemclick", "onitemselect", "textField", "idField", "parentField"]);
        mini["_ParseBool"](_, E, ["resultAsTree"]);
        var A = mini["getChildNodes"](_),
		$ = this.parseItems(A);
        if ($.length > 0) E.items = $;
        var B = D.attr("vertical");
        if (B) E.vertical = B == "true" ? true : false;
        var C = D.attr("allowSelectItem");
        if (C) E.allowSelectItem = C == "true" ? true : false;
        return E
    }
});
HbyG(mini.Menu, "menu");
OECBar = function () {
    OECBar["superclass"]["constructor"]["call"](this)
};
Pv_r(OECBar, mini.Menu, {
    uiCls: "mini-menubar",
    vertical: false,
    setVertical: function ($) {
        this.vertical = false
    }
});
HbyG(OECBar, "menubar");
mini.ContextMenu = function () {
    mini.ContextMenu["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.ContextMenu, mini.Menu, {
    uiCls: "mini-contextmenu",
    vertical: true,
    visible: false,
    setVertical: function ($) {
        this.vertical = true
    }
});
HbyG(mini.ContextMenu, "contextmenu");
mini.MenuItem = function () {
    mini.MenuItem["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.MenuItem, mini.Control, {
    text: "",
    iconCls: "",
    iconStyle: "",
    iconPosition: "left",
    showIcon: true,
    showAllow: true,
    checked: false,
    checkOnClick: false,
    groupName: "",
    _hoverCls: "mini-menuitem-hover",
    ZyR: "mini-menuitem-pressed",
    R2Z: "mini-menuitem-checked",
    _clearBorder: false,
    menu: null,
    uiCls: "mini-menuitem",
    _create: function () {
        var $ = this.el = document.createElement("div");
        this.el.className = "mini-menuitem";
        this.el.innerHTML = "<div class=\"mini-menuitem-inner\"><div class=\"mini-menuitem-icon\"></div><div class=\"mini-menuitem-text\"></div><div class=\"mini-menuitem-allow\"></div></div>";
        this.N$i = this.el.firstChild;
        this.Xk = this.N$i.firstChild;
        this.VXt = this.N$i.childNodes[1];
        this.allowEl = this.N$i.lastChild
    },
    _initEvents: function () {
        VNV(this.el, "click", this.YY, this);
        VNV(this.el, "mouseup", this.ZXp, this);
        VNV(this.el, "mouseover", this.Vv5, this);
        VNV(this.el, "mouseout", this.Gj$, this)
    },
    destroy: function ($) {
        this.menu = null;
        mini.MenuItem["superclass"]["destroy"]["call"](this, $)
    },
    within: function ($) {
        if (Yma(this.el, $.target)) return true;
        if (this.menu && this.menu["within"]($)) return true;
        return false
    },
    doUpdate: function () {
        if (this.VXt) this.VXt.innerHTML = this.text;
        if (this.Xk) {
            BIT(this.Xk, this["iconStyle"]);
            RE(this.Xk, this.iconCls);
            this.Xk.style.display = (this["iconStyle"] || this.iconCls) ? "block" : "none"
        }
        if (this.iconPosition == "top") RE(this.el, "mini-menuitem-icontop");
        else WeL(this.el, "mini-menuitem-icontop");
        if (this.checked) RE(this.el, this.R2Z);
        else WeL(this.el, this.R2Z);
        if (this.allowEl) if (this.menu && this.menu.items.length > 0) this.allowEl.style.display = "block";
        else this.allowEl.style.display = "none"
    },
    setText: function ($) {
        this.text = $;
        this["doUpdate"]()
    },
    getText: function () {
        return this.text
    },
    setIconCls: function ($) {
        WeL(this.Xk, this.iconCls);
        this.iconCls = $;
        this["doUpdate"]()
    },
    getIconCls: function () {
        return this.iconCls
    },
    setIconStyle: function ($) {
        this["iconStyle"] = $;
        this["doUpdate"]()
    },
    getIconStyle: function () {
        return this["iconStyle"]
    },
    setIconPosition: function ($) {
        this.iconPosition = $;
        this["doUpdate"]()
    },
    getIconPosition: function () {
        return this.iconPosition
    },
    setCheckOnClick: function ($) {
        this["checkOnClick"] = $;
        if ($) RE(this.el, "mini-menuitem-showcheck");
        else WeL(this.el, "mini-menuitem-showcheck")
    },
    getCheckOnClick: function () {
        return this["checkOnClick"]
    },
    setChecked: function ($) {
        if (this.checked != $) {
            this.checked = $;
            this["doUpdate"]();
            this.fire("checkedchanged")
        }
    },
    getChecked: function () {
        return this.checked
    },
    setGroupName: function ($) {
        if (this["groupName"] != $) this["groupName"] = $
    },
    getGroupName: function () {
        return this["groupName"]
    },
    setChildren: function ($) {
        this.setMenu($)
    },
    setMenu: function ($) {
        if (mini.isArray($)) $ = {
            type: "menu",
            items: $
        };
        if (this.menu !== $) {
            this.menu = mini.getAndCreate($);
            this.menu.hide();
            this.menu.ownerItem = this;
            this["doUpdate"]();
            this.menu.on("itemschanged", this.OK, this)
        }
    },
    getMenu: function () {
        return this.menu
    },
    showMenu: function () {
        if (this.menu) {
            this.menu.setHideAction("outerclick");
            var $ = {
                hAlign: "outright",
                vAlign: "top",
                outHAlign: "outleft",
                popupCls: "mini-menu-popup"
            };
            if (this.ownerMenu && this.ownerMenu.vertical == false) {
                $.hAlign = "left";
                $.vAlign = "below";
                $.outHAlign = null
            }
            this.menu.showAtEl(this.el, $)
        }
    },
    hideMenu: function () {
        if (this.menu) this.menu.hide()
    },
    hide: function () {
        this.hideMenu();
        this["setVisible"](false)
    },
    OK: function ($) {
        this["doUpdate"]()
    },
    getTopMenu: function () {
        if (this.ownerMenu) if (this.ownerMenu.ownerItem) return this.ownerMenu.ownerItem.getTopMenu();
        else return this.ownerMenu;
        return null
    },
    YY: function (D) {
        if (this["isReadOnly"]()) return;
        if (this["checkOnClick"]) if (this.ownerMenu && this["groupName"]) {
            var B = this.ownerMenu.getGroupItems(this["groupName"]);
            if (B.length > 0) {
                if (this.checked == false) {
                    for (var _ = 0, C = B.length; _ < C; _++) {
                        var $ = B[_];
                        if ($ != this) $.setChecked(false)
                    }
                    this.setChecked(true)
                }
            } else this.setChecked(!this.checked)
        } else this.setChecked(!this.checked);
        this.fire("click");
        var A = this.getTopMenu();
        if (A) A._OnItemClick(this, D)
    },
    ZXp: function (_) {
        if (this["isReadOnly"]()) return;
        if (this.ownerMenu) {
            var $ = this;
            setTimeout(function () {
                if ($["isDisplay"]()) $.ownerMenu.showItemMenu($)
            },
			1)
        }
    },
    Vv5: function ($) {
        if (this["isReadOnly"]()) return;
        RE(this.el, this._hoverCls);
        if (this.ownerMenu) if (this.ownerMenu.isVertical() == true) this.ownerMenu.showItemMenu(this);
        else if (this.ownerMenu.hasShowItemMenu()) this.ownerMenu.showItemMenu(this)
    },
    Gj$: function ($) {
        WeL(this.el, this._hoverCls)
    },
    onClick: function (_, $) {
        this.on("click", _, $)
    },
    onCheckedChanged: function (_, $) {
        this.on("checkedchanged", _, $)
    },
    getAttrs: function ($) {
        var A = mini.MenuItem["superclass"]["getAttrs"]["call"](this, $),
		_ = jQuery($);
        A.text = $.innerHTML;
        mini["_ParseString"]($, A, ["text", "iconCls", "iconStyle", "iconPosition", "groupName", "onclick", "oncheckedchanged"]);
        mini["_ParseBool"]($, A, ["checkOnClick", "checked"]);
        return A
    }
});
HbyG(mini.MenuItem, "menuitem");
mini.OutlookBar = function () {
    this.FL7();
    mini.OutlookBar["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.OutlookBar, mini.Control, {
    width: 180,
    activeIndex: -1,
    autoCollapse: false,
    groupCls: "",
    groupStyle: "",
    groupHeaderCls: "",
    groupHeaderStyle: "",
    groupBodyCls: "",
    groupBodyStyle: "",
    groupHoverCls: "",
    groupActiveCls: "",
    allowAnim: true,
    set: function (_) {
        if (typeof _ == "string") return this;
        var A = this.SX1V;
        this.SX1V = false;
        var $ = _.activeIndex;
        delete _.activeIndex;
        mini.OutlookBar["superclass"].set["call"](this, _);
        if (mini.isNumber($)) this.setActiveIndex($);
        this.SX1V = A;
        this["doLayout"]();
        return this
    },
    uiCls: "mini-outlookbar",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-outlookbar";
        this.el.innerHTML = "<div class=\"mini-outlookbar-border\"></div>";
        this._firstChild = this.el.firstChild
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "click", this.YY, this)
        },
		this)
    },
    Xbh: function ($) {
        return this.uid + "$" + $._id
    },
    _GroupId: 1,
    FL7: function () {
        this.groups = []
    },
    $_P: function (_) {
        var H = this.Xbh(_),
		G = "<div id=\"" + H + "\" class=\"mini-outlookbar-group " + _.cls + "\" style=\"" + _.style + "\">" + "<div class=\"mini-outlookbar-groupHeader " + _.headerCls + "\" style=\"" + _.headerStyle + ";\"></div>" + "<div class=\"mini-outlookbar-groupBody " + _.bodyCls + "\" style=\"" + _.bodyStyle + ";\"></div>" + "</div>",
		A = mini.append(this._firstChild, G),
		E = A.lastChild,
		C = _.body;
        delete _.body;
        if (C) {
            if (!mini.isArray(C)) C = [C];
            for (var $ = 0, F = C.length; $ < F; $++) {
                var B = C[$];
                mini.append(E, B)
            }
            C.length = 0
        }
        if (_.bodyParent) {
            var D = _.bodyParent;
            while (D.firstChild) E.appendChild(D.firstChild)
        }
        delete _.bodyParent;
        return A
    },
    createGroup: function (_) {
        var $ = mini.copyTo({
            _id: this._GroupId++,
            name: "",
            title: "",
            cls: "",
            style: "",
            iconCls: "",
            iconStyle: "",
            headerCls: "",
            headerStyle: "",
            bodyCls: "",
            bodyStyle: "",
            visible: true,
            enabled: true,
            showCollapseButton: true,
            expanded: true
        },
		_);
        return $
    },
    setGroups: function (_) {
        if (!mini.isArray(_)) return;
        this.removeAll();
        for (var $ = 0, A = _.length; $ < A; $++) this.addGroup(_[$])
    },
    getGroups: function () {
        return this.groups
    },
    addGroup: function (_, $) {
        if (typeof _ == "string") _ = {
            title: _
        };
        _ = this.createGroup(_);
        if (typeof $ != "number") $ = this.groups.length;
        this.groups.insert($, _);
        var B = this.$_P(_);
        _._el = B;
        var $ = this.groups.indexOf(_),
		A = this.groups[$ + 1];
        if (A) {
            var C = this.getGroupEl(A);
            jQuery(C).before(B)
        }
        this["doUpdate"]();
        return _
    },
    updateGroup: function ($, _) {
        var $ = this.getGroup($);
        if (!$) return;
        mini.copyTo($, _);
        this["doUpdate"]()
    },
    removeGroup: function ($) {
        $ = this.getGroup($);
        if (!$) return;
        var _ = this.getGroupEl($);
        if (_) _.parentNode.removeChild(_);
        this.groups.remove($);
        this["doUpdate"]()
    },
    removeAll: function () {
        for (var $ = this.groups.length - 1; $ >= 0; $--) this.removeGroup($)
    },
    moveGroup: function (_, $) {
        _ = this.getGroup(_);
        if (!_) return;
        target = this.getGroup($);
        var A = this.getGroupEl(_);
        this.groups.remove(_);
        if (target) {
            $ = this.groups.indexOf(target);
            this.groups.insert($, _);
            var B = this.getGroupEl(target);
            jQuery(B).before(A)
        } else {
            this.groups.add(_);
            this._firstChild.appendChild(A)
        }
        this["doUpdate"]()
    },
    doUpdate: function () {
        for (var _ = 0, E = this.groups.length; _ < E; _++) {
            var A = this.groups[_],
			B = A._el,
			D = B.firstChild,
			C = B.lastChild,
			$ = "<div class=\"mini-outlookbar-icon " + A.iconCls + "\" style=\"" + A["iconStyle"] + ";\"></div>",
			F = "<div class=\"mini-tools\"><span class=\"mini-tools-collapse\"></span></div>" + ((A["iconStyle"] || A.iconCls) ? $ : "") + "<div class=\"mini-outlookbar-groupTitle\">" + A.title + "</div><div style=\"clear:both;\"></div>";
            D.innerHTML = F;
            if (A.enabled) WeL(B, "mini-disabled");
            else RE(B, "mini-disabled");
            RE(B, A.cls);
            BIT(B, A.style);
            RE(C, A.bodyCls);
            BIT(C, A.bodyStyle);
            RE(D, A.headerCls);
            BIT(D, A.headerStyle);
            WeL(B, "mini-outlookbar-firstGroup");
            WeL(B, "mini-outlookbar-lastGroup");
            if (_ == 0) RE(B, "mini-outlookbar-firstGroup");
            if (_ == E - 1) RE(B, "mini-outlookbar-lastGroup")
        }
        this["doLayout"]()
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        if (this._g) return;
        this.P0();
        for (var $ = 0, H = this.groups.length; $ < H; $++) {
            var _ = this.groups[$],
			B = _._el,
			D = B.lastChild;
            if (_.expanded) {
                RE(B, "mini-outlookbar-expand");
                WeL(B, "mini-outlookbar-collapse")
            } else {
                WeL(B, "mini-outlookbar-expand");
                RE(B, "mini-outlookbar-collapse")
            }
            D.style.height = "auto";
            D.style.display = _.expanded ? "block" : "none";
            B.style.display = _.visible ? "" : "none";
            var A = L_h(B, true),
			E = M32(D),
			G = Ly5(D);
            if (jQuery.boxModel) A = A - E.left - E.right - G.left - G.right;
            D.style.width = A + "px"
        }
        var F = this["isAutoHeight"](),
		C = this.getActiveGroup();
        if (!F && this["autoCollapse"] && C) {
            B = this.getGroupEl(this.activeIndex);
            B.lastChild.style.height = this.ZsjE() + "px"
        }
        mini.layout(this._firstChild)
    },
    P0: function () {
        if (this["isAutoHeight"]()) this._firstChild.style.height = "auto";
        else {
            var $ = this["getHeight"](true);
            if (!jQuery.boxModel) {
                var _ = Ly5(this._firstChild);
                $ = $ + _.top + _.bottom
            }
            if ($ < 0) $ = 0;
            this._firstChild.style.height = $ + "px"
        }
    },
    ZsjE: function () {
        var C = jQuery(this.el).height(),
		K = Ly5(this._firstChild);
        C = C - K.top - K.bottom;
        var A = this.getActiveGroup(),
		E = 0;
        for (var F = 0, D = this.groups.length; F < D; F++) {
            var _ = this.groups[F],
			G = this.getGroupEl(_);
            if (_.visible == false || _ == A) continue;
            var $ = G.lastChild.style.display;
            G.lastChild.style.display = "none";
            var J = jQuery(G).outerHeight();
            G.lastChild.style.display = $;
            var L = OFY(G);
            J = J + L.top + L.bottom;
            E += J
        }
        C = C - E;
        var H = this.getGroupEl(this.activeIndex);
        C = C - jQuery(H.firstChild).outerHeight();
        if (jQuery.boxModel) {
            var B = M32(H.lastChild),
			I = Ly5(H.lastChild);
            C = C - B.top - B.bottom - I.top - I.bottom
        }
        B = M32(H),
		I = Ly5(H),
		L = OFY(H);
        C = C - L.top - L.bottom;
        C = C - B.top - B.bottom - I.top - I.bottom;
        if (C < 0) C = 0;
        return C
    },
    getGroup: function ($) {
        if (typeof $ == "object") return $;
        if (typeof $ == "number") return this.groups[$];
        else for (var _ = 0, B = this.groups.length; _ < B; _++) {
            var A = this.groups[_];
            if (A.name == $) return A
        }
    },
    MBK: function (B) {
        for (var $ = 0, A = this.groups.length; $ < A; $++) {
            var _ = this.groups[$];
            if (_._id == B) return _
        }
    },
    getGroupEl: function ($) {
        var _ = this.getGroup($);
        if (!_) return null;
        return _._el
    },
    getGroupBodyEl: function ($) {
        var _ = this.getGroupEl($);
        if (_) return _.lastChild;
        return null
    },
    setAutoCollapse: function ($) {
        this["autoCollapse"] = $
    },
    getAutoCollapse: function () {
        return this["autoCollapse"]
    },
    setActiveIndex: function (_) {
        var $ = this.getGroup(_),
		A = this.getGroup(this.activeIndex),
		B = $ != A;
        if ($) this.activeIndex = this.groups.indexOf($);
        else this.activeIndex = -1;
        $ = this.getGroup(this.activeIndex);
        if ($) {
            var C = this.allowAnim;
            this.allowAnim = false;
            this.expandGroup($);
            this.allowAnim = C
        }
    },
    getActiveIndex: function () {
        return this.activeIndex
    },
    getActiveGroup: function () {
        return this.getGroup(this.activeIndex)
    },
    showGroup: function ($) {
        $ = this.getGroup($);
        if (!$ || $.visible == true) return;
        $.visible = true;
        this["doUpdate"]()
    },
    hideGroup: function ($) {
        $ = this.getGroup($);
        if (!$ || $.visible == false) return;
        $.visible = false;
        this["doUpdate"]()
    },
    toggleGroup: function ($) {
        $ = this.getGroup($);
        if (!$) return;
        if ($.expanded) this.collapseGroup($);
        else this.expandGroup($)
    },
    collapseGroup: function (_) {
        _ = this.getGroup(_);
        if (!_) return;
        var D = _.expanded,
		E = 0;
        if (this["autoCollapse"] && !this["isAutoHeight"]()) E = this.ZsjE();
        var F = false;
        _.expanded = false;
        var $ = this.groups.indexOf(_);
        if ($ == this.activeIndex) {
            this.activeIndex = -1;
            F = true
        }
        var C = this.getGroupBodyEl(_);
        if (this.allowAnim && D) {
            this._g = true;
            C.style.display = "block";
            C.style.height = "auto";
            if (this["autoCollapse"] && !this["isAutoHeight"]()) C.style.height = E + "px";
            var A = {
                height: "1px"
            };
            RE(C, "mini-outlookbar-overflow");
            var B = this,
			H = jQuery(C);
            H.animate(A, 180,
			function () {
			    B._g = false;
			    WeL(C, "mini-outlookbar-overflow");
			    B["doLayout"]()
			})
        } else this["doLayout"]();
        var G = {
            group: _,
            index: this.groups.indexOf(_),
            name: _.name
        };
        this.fire("Collapse", G);
        if (F) this.fire("activechanged")
    },
    expandGroup: function ($) {
        $ = this.getGroup($);
        if (!$) return;
        var H = $.expanded;
        $.expanded = true;
        this.activeIndex = this.groups.indexOf($);
        fire = true;
        if (this["autoCollapse"]) for (var D = 0, B = this.groups.length; D < B; D++) {
            var C = this.groups[D];
            if (C.expanded && C != $) this.collapseGroup(C)
        }
        var G = this.getGroupBodyEl($);
        if (this.allowAnim && H == false) {
            this._g = true;
            G.style.display = "block";
            if (this["autoCollapse"] && !this["isAutoHeight"]()) {
                var A = this.ZsjE();
                G.style.height = (A) + "px"
            } else G.style.height = "auto";
            var _ = Nf(G);
            G.style.height = "1px";
            var E = {
                height: _ + "px"
            },
			I = G.style.overflow;
            G.style.overflow = "hidden";
            RE(G, "mini-outlookbar-overflow");
            var F = this,
			K = jQuery(G);
            K.animate(E, 180,
			function () {
			    G.style.overflow = I;
			    WeL(G, "mini-outlookbar-overflow");
			    F._g = false;
			    F["doLayout"]()
			})
        } else this["doLayout"]();
        var J = {
            group: $,
            index: this.groups.indexOf($),
            name: $.name
        };
        this.fire("Expand", J);
        if (fire) this.fire("activechanged")
    },
    Uc: function ($) {
        $ = this.getGroup($);
        var _ = {
            group: $,
            groupIndex: this.groups.indexOf($),
            groupName: $.name,
            cancel: false
        };
        if ($.expanded) {
            this.fire("BeforeCollapse", _);
            if (_.cancel == false) this.collapseGroup($)
        } else {
            this.fire("BeforeExpand", _);
            if (_.cancel == false) this.expandGroup($)
        }
    },
    Zw: function (B) {
        var _ = MRj9(B.target, "mini-outlookbar-group");
        if (!_) return null;
        var $ = _.id.split("$"),
		A = $[$.length - 1];
        return this.MBK(A)
    },
    YY: function (A) {
        if (this._g) return;
        var _ = MRj9(A.target, "mini-outlookbar-groupHeader");
        if (!_) return;
        var $ = this.Zw(A);
        if (!$) return;
        this.Uc($)
    },
    parseGroups: function (D) {
        var A = [];
        for (var $ = 0, C = D.length; $ < C; $++) {
            var B = D[$],
			_ = {};
            A.push(_);
            _.style = B.style.cssText;
            mini["_ParseString"](B, _, ["name", "title", "cls", "iconCls", "iconStyle", "headerCls", "headerStyle", "bodyCls", "bodyStyle"]);
            mini["_ParseBool"](B, _, ["visible", "enabled", "showCollapseButton", "expanded"]);
            _.bodyParent = B
        }
        return A
    },
    getAttrs: function ($) {
        var A = mini.OutlookBar["superclass"]["getAttrs"]["call"](this, $);
        mini["_ParseString"]($, A, ["onactivechanged", "oncollapse", "onexpand"]);
        mini["_ParseBool"]($, A, ["autoCollapse", "allowAnim"]);
        mini["_ParseInt"]($, A, ["activeIndex"]);
        var _ = mini["getChildNodes"]($);
        A.groups = this.parseGroups(_);
        return A
    }
});
HbyG(mini.OutlookBar, "outlookbar");
mini.OutlookMenu = function () {
    mini.OutlookMenu["superclass"]["constructor"]["call"](this);
    this.data = []
};
Pv_r(mini.OutlookMenu, mini.OutlookBar, {
    url: "",
    textField: "text",
    iconField: "iconCls",
    urlField: "url",
    resultAsTree: false,
    itemsField: "children",
    idField: "id",
    parentField: "pid",
    style: "width:100%;height:100%;",
    set: function (_) {
        if (typeof _ == "string") return this;
        var A = _.url;
        delete _.url;
        var $ = _.activeIndex;
        delete _.activeIndex;
        mini.OutlookMenu["superclass"].set["call"](this, _);
        if (A) this.setUrl(A);
        if (mini.isNumber($)) this.setActiveIndex($);
        return this
    },
    uiCls: "mini-outlookmenu",
    destroy: function (B) {
        if (this.V3) {
            var _ = this.V3.clone();
            for (var $ = 0, C = _.length; $ < C; $++) {
                var A = _[$];
                A["destroy"]()
            }
            this.V3.length = 0
        }
        mini.OutlookMenu["superclass"]["destroy"]["call"](this, B)
    },
    Sc: function () {
        var B = mini.getData(this.url);
        if (!B) B = [];
        if (this["resultAsTree"] == false) B = mini.arrayToTree(B, this.itemsField, this.idField, this["parentField"]);
        var _ = mini["treeToArray"](B, this.itemsField, this.idField, this["parentField"]);
        for (var A = 0, C = _.length; A < C; A++) {
            var $ = _[A];
            $.text = $[this.textField];
            $.url = $[this.urlField];
            $.iconCls = $[this.iconField]
        }
        this.createNavBarMenu(B);
        this.fire("load")
    },
    load: function ($) {
        if (typeof $ == "string") this.setUrl($);
        else this.createNavBarMenu($)
    },
    setUrl: function ($) {
        this.url = $;
        this.Sc()
    },
    getUrl: function () {
        return this.url
    },
    setTextField: function ($) {
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    setIconField: function ($) {
        this.iconField = $
    },
    getIconField: function () {
        return this.iconField
    },
    setUrlField: function ($) {
        this["urlField"] = $
    },
    getUrlField: function () {
        return this["urlField"]
    },
    setResultAsTree: function ($) {
        this["resultAsTree"] = $
    },
    getResultAsTree: function () {
        return this["resultAsTree"]
    },
    setNodesField: function ($) {
        this.nodesField = $
    },
    getNodesField: function () {
        return this.nodesField
    },
    setIdField: function ($) {
        this["idField"] = $
    },
    getIdField: function () {
        return this["idField"]
    },
    setParentField: function ($) {
        this["parentField"] = $
    },
    getParentField: function () {
        return this["parentField"]
    },
    RrP: null,
    getSelected: function () {
        return this.RrP
    },
    getAttrs: function ($) {
        var _ = mini.OutlookMenu["superclass"]["getAttrs"]["call"](this, $);
        _.text = $.innerHTML;
        mini["_ParseString"]($, _, ["url", "textField", "urlField", "idField", "parentField", "itemsField", "iconField", "onitemclick", "onitemselect"]);
        mini["_ParseBool"]($, _, ["resultAsTree"]);
        return _
    },
    autoCollapse: true,
    activeIndex: 0,
    createNavBarMenu: function (D) {
        if (!mini.isArray(D)) D = [];
        this.data = D;
        var B = [];
        for (var _ = 0, E = this.data.length; _ < E; _++) {
            var $ = this.data[_],
			A = {};
            A.title = $.text;
            A.titleCls = $.iconCls;
            B.push(A);
            A._children = $[this.itemsField]
        }
        this.setGroups(B);
        this.setActiveIndex(this.activeIndex);
        this.V3 = [];
        for (_ = 0, E = this.groups.length; _ < E; _++) {
            var A = this.groups[_],
			C = this.getGroupBodyEl(A),
			F = new mini.Menu();
            F.set({
                style: "width:100%;height:100%;border:0;background:none",
                allowSelectItem: true,
                items: A._children
            });
            F["render"](C);
            F.on("itemclick", this.P3, this);
            F.on("itemselect", this.PlR, this);
            this.V3.push(F);
            delete A._children
        }
    },
    P3: function (_) {
        var $ = {
            item: _.item,
            htmlEvent: _.htmlEvent
        };
        this.fire("itemclick", $)
    },
    PlR: function (C) {
        if (!C.item) return;
        for (var $ = 0, A = this.V3.length; $ < A; $++) {
            var B = this.V3[$];
            if (B != C.sender) B.setSelectedItem(null)
        }
        var _ = {
            item: C.item,
            htmlEvent: C.htmlEvent
        };
        this.RrP = C.item;
        this.fire("itemselect", _)
    }
});
HbyG(mini.OutlookMenu, "outlookmenu");
mini.OutlookTree = function () {
    mini.OutlookTree["superclass"]["constructor"]["call"](this);
    this.data = []
};
Pv_r(mini.OutlookTree, mini.OutlookBar, {
    url: "",
    textField: "text",
    iconField: "iconCls",
    urlField: "url",
    resultAsTree: false,
    nodesField: "children",
    idField: "id",
    parentField: "pid",
    style: "width:100%;height:100%;",
    set: function (_) {
        if (typeof _ == "string") return this;
        var A = _.url;
        delete _.url;
        var $ = _.activeIndex;
        delete _.activeIndex;
        mini.OutlookTree["superclass"].set["call"](this, _);
        if (A) this.setUrl(A);
        if (mini.isNumber($)) this.setActiveIndex($);
        return this
    },
    uiCls: "mini-outlooktree",
    destroy: function (B) {
        if (this.Vu9) {
            var _ = this.Vu9.clone();
            for (var $ = 0, C = _.length; $ < C; $++) {
                var A = _[$];
                A["destroy"]()
            }
            this.Vu9.length = 0
        }
        mini.OutlookTree["superclass"]["destroy"]["call"](this, B)
    },
    Sc: function () {
        var B = mini.getData(this.url);
        if (!B) B = [];
        if (this["resultAsTree"] == false) B = mini.arrayToTree(B, this.nodesField, this.idField, this["parentField"]);
        var _ = mini["treeToArray"](B, this.nodesField, this.idField, this["parentField"]);
        for (var A = 0, C = _.length; A < C; A++) {
            var $ = _[A];
            $.text = $[this.textField];
            $.url = $[this.urlField];
            $.iconCls = $[this.iconField]
        }
        this.createNavBarTree(B);
        this.fire("load")
    },
    load: function ($) {
        if (typeof $ == "string") this.setUrl($);
        else this.createNavBarTree($)
    },
    setUrl: function ($) {
        this.url = $;
        this.Sc()
    },
    getUrl: function () {
        return this.url
    },
    setTextField: function ($) {
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    setIconField: function ($) {
        this.iconField = $
    },
    getIconField: function () {
        return this.iconField
    },
    setUrlField: function ($) {
        this["urlField"] = $
    },
    getUrlField: function () {
        return this["urlField"]
    },
    setResultAsTree: function ($) {
        this["resultAsTree"] = $
    },
    getResultAsTree: function () {
        return this["resultAsTree"]
    },
    setNodesField: function ($) {
        this.nodesField = $
    },
    getNodesField: function () {
        return this.nodesField
    },
    setIdField: function ($) {
        this["idField"] = $
    },
    getIdField: function () {
        return this["idField"]
    },
    setParentField: function ($) {
        this["parentField"] = $
    },
    getParentField: function () {
        return this["parentField"]
    },
    RrP: null,
    getSelected: function () {
        return this.RrP
    },
    selectNode: function (_) {
        _ = this["getNode"](_);
        if (!_) return;
        var $ = this._getOwnerTree(_);
        $.selectNode(_)
    },
    expandPath: function (_) {
        _ = this["getNode"](_);
        if (!_) return;
        var $ = this._getOwnerTree(_);
        $.expandPath(_);
        this.expandGroup($._ownerGroup)
    },
    getNode: function (A) {
        for (var $ = 0, C = this.Vu9.length; $ < C; $++) {
            var _ = this.Vu9[$],
			B = _["getNode"](A);
            if (B) return B
        }
        return null
    },
    _getOwnerTree: function (A) {
        if (!A) return;
        for (var $ = 0, B = this.Vu9.length; $ < B; $++) {
            var _ = this.Vu9[$];
            if (_.C4G[A._id]) return _
        }
    },
    expandOnLoad: false,
    setExpandOnLoad: function ($) {
        this.expandOnLoad = $
    },
    getExpandOnLoad: function () {
        return this.expandOnLoad
    },
    getAttrs: function (_) {
        var A = mini.OutlookTree["superclass"]["getAttrs"]["call"](this, _);
        A.text = _.innerHTML;
        mini["_ParseString"](_, A, ["url", "textField", "urlField", "idField", "parentField", "nodesField", "iconField", "onnodeclick", "onnodeselect", "onnodemousedown", "expandOnLoad"]);
        mini["_ParseBool"](_, A, ["resultAsTree"]);
        if (A.expandOnLoad) {
            var $ = parseInt(A.expandOnLoad);
            if (mini.isNumber($)) A.expandOnLoad = $;
            else A.expandOnLoad = A.expandOnLoad == "true" ? true : false
        }
        return A
    },
    autoCollapse: true,
    activeIndex: 0,
    createNavBarTree: function (D) {
        if (!mini.isArray(D)) D = [];
        this.data = D;
        var B = [];
        for (var _ = 0, E = this.data.length; _ < E; _++) {
            var $ = this.data[_],
			A = {};
            A.title = $.text;
            A.titleCls = $.iconCls;
            B.push(A);
            A._children = $[this.nodesField]
        }
        this.setGroups(B);
        this.setActiveIndex(this.activeIndex);
        this.Vu9 = [];
        for (_ = 0, E = this.groups.length; _ < E; _++) {
            var A = this.groups[_],
			C = this.getGroupBodyEl(A),
			D = new mini.Tree();
            D.set({
                expandOnLoad: this.expandOnLoad,
                showTreeIcon: true,
                style: "width:100%;height:100%;border:0;background:none",
                data: A._children
            });
            D["render"](C);
            D.on("nodeclick", this.Gs38, this);
            D.on("nodeselect", this.P3g, this);
            D.on("nodemousedown", this.__OnNodeMouseDown, this);
            this.Vu9.push(D);
            delete A._children;
            D._ownerGroup = A
        }
    },
    __OnNodeMouseDown: function (_) {
        var $ = {
            node: _.node,
            isLeaf: _.sender.isLeaf(_.node),
            htmlEvent: _.htmlEvent
        };
        this.fire("nodemousedown", $)
    },
    Gs38: function (_) {
        var $ = {
            node: _.node,
            isLeaf: _.sender.isLeaf(_.node),
            htmlEvent: _.htmlEvent
        };
        this.fire("nodeclick", $)
    },
    P3g: function (C) {
        if (!C.node) return;
        for (var $ = 0, B = this.Vu9.length; $ < B; $++) {
            var A = this.Vu9[$];
            if (A != C.sender) A.selectNode(null)
        }
        var _ = {
            node: C.node,
            isLeaf: C.sender.isLeaf(C.node),
            htmlEvent: C.htmlEvent
        };
        this.RrP = C.node;
        this.fire("nodeselect", _)
    }
});
HbyG(mini.OutlookTree, "outlooktree");
mini.NavBar = function () {
    mini.NavBar["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.NavBar, mini.OutlookBar, {
    uiCls: "mini-navbar"
});
HbyG(mini.NavBar, "navbar");
mini.NavBarMenu = function () {
    mini.NavBarMenu["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.NavBarMenu, mini.OutlookMenu, {
    uiCls: "mini-navbarmenu"
});
HbyG(mini.NavBarMenu, "navbarmenu");
mini.NavBarTree = function () {
    mini.NavBarTree["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.NavBarTree, mini.OutlookTree, {
    uiCls: "mini-navbartree"
});
HbyG(mini.NavBarTree, "navbartree");
mini.ToolBar = function () {
    mini.ToolBar["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.ToolBar, mini.Control, {
    _clearBorder: false,
    style: "",
    uiCls: "mini-toolbar",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-toolbar"
    },
    _initEvents: function () { },
    doLayout: function () {
        if (!this.canLayout()) return;
        var A = mini["getChildNodes"](this.el, true);
        for (var $ = 0, _ = A.length; $ < _; $++) mini.layout(A[$])
    },
    set_bodyParent: function ($) {
        if (!$) return;
        this.el = $;
        this["doLayout"]()
    },
    getAttrs: function ($) {
        var _ = {};
        mini["_ParseString"]($, _, ["id", "borderStyle"]);
        this.el = $;
        this.el.uid = this.uid;
        return _
    }
});
HbyG(mini.ToolBar, "toolbar");
mini.Tree = function ($) {
    this._ajaxOption = {
        async: false,
        type: "get"
    };
    this.root = {
        _id: -1,
        _pid: "",
        _level: -1
    };
    this.data = this.root[this.nodesField] = [];
    this.C4G = {};
    this.NiWt = {};
    this._viewNodes = null;
    mini.Tree["superclass"]["constructor"]["call"](this, $);
    this.on("beforeexpand",
	function (B) {
	    var $ = B.node,
		A = this.isLeaf($),
		_ = $[this.nodesField];
	    if (!A && (!_ || _.length == 0)) {
	        B.cancel = true;
	        this.loadNode($)
	    }
	},
	this);
    this["doUpdate"]()
};
mini.Tree.NodeUID = 1;
var lastNodeLevel = [];
Pv_r(mini.Tree, mini.Control, {
    isTree: true,
    U60X: "block",
    removeOnCollapse: true,
    expandOnDblClick: true,
    value: "",
    PI3: null,
    allowSelect: true,
    showCheckBox: false,
    showFolderCheckBox: true,
    showExpandButtons: true,
    enableHotTrack: true,
    showArrow: false,
    expandOnLoad: false,
    delimiter: ",",
    url: "",
    root: null,
    resultAsTree: true,
    parentField: "pid",
    idField: "id",
    textField: "text",
    iconField: "iconCls",
    nodesField: "children",
    showTreeIcon: false,
    showTreeLines: true,
    checkRecursive: false,
    allowAnim: true,
    $Sn: "mini-tree-checkbox",
    U4bQ: "mini-tree-selectedNode",
    UdPE: "mini-tree-node-hover",
    leafIcon: "mini-tree-leaf",
    folderIcon: "mini-tree-folder",
    ZeR: "mini-tree-border",
    X34: "mini-tree-header",
    E5e: "mini-tree-body",
    ME$h: "mini-tree-node",
    Xu: "mini-tree-nodes",
    IpN: "mini-tree-expand",
    CA6: "mini-tree-collapse",
    SR: "mini-tree-node-ecicon",
    WnSi: "mini-tree-nodeshow",
    set: function (A) {
        if (typeof A == "string") return this;
        var $ = A.value;
        delete A.value;
        var B = A.url;
        delete A.url;
        var _ = A.data;
        delete A.data;
        mini.Tree["superclass"].set["call"](this, A);
        if (!mini.isNull(_)) this["setData"](_);
        if (!mini.isNull(B)) this.setUrl(B);
        if (!mini.isNull($)) this["setValue"]($);
        return this
    },
    uiCls: "mini-tree",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-tree";
        if (this["showTreeLines"] == true) RE(this.el, "mini-tree-treeLine");
        this.el.style.display = "block";
        this._firstChild = mini.append(this.el, "<div class=\"" + this.ZeR + "\">" + "<div class=\"" + this.X34 + "\"></div><div class=\"" + this.E5e + "\"></div></div>");
        this.VEE = this._firstChild.childNodes[0];
        this.$kJ = this._firstChild.childNodes[1];
        this._DragDrop = new E2(this)
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "click", this.YY, this);
            VNV(this.el, "dblclick", this.E8S, this);
            VNV(this.el, "mousedown", this.UNAE, this);
            VNV(this.el, "mousemove", this.A$Z, this);
            VNV(this.el, "mouseout", this.Gj$, this)
        },
		this)
    },
    load: function ($) {
        if (typeof $ == "string") {
            this.url = $;
            this.Sc({},
			this.root)
        } else this["setData"]($)
    },
    setData: function ($) {
        this["loadData"]($);
        this.data = $;
        this._cellErrors = [];
        this._cellMapErrors = {}
    },
    getData: function () {
        return this.data
    },
    toArray: function () {
        return this.getList()
    },
    getList: function () {
        if (!this.XQ) {
            this.XQ = mini["treeToArray"](this.root[this.nodesField], this.nodesField, this.idField, this.parentField, "-1");
            this._indexs = {};
            for (var $ = 0, A = this.XQ.length; $ < A; $++) {
                var _ = this.XQ[$];
                this._indexs[_[this.idField]] = $
            }
        }
        return this.XQ
    },
    _clearTree: function () {
        this.XQ = null;
        this._indexs = null
    },
    loadData: function ($) {
        if (!mini.isArray($)) $ = [];
        this.root[this.nodesField] = $;
        this.RFtO(this.root, null);
        this.cascadeChild(this.root,
		function (_) {
		    if (mini.isNull(_.expanded)) {
		        var $ = this.getLevel(_);
		        if (this.expandOnLoad === true || (mini.isNumber(this.expandOnLoad) && $ <= this.expandOnLoad)) _.expanded = true;
		        else _.expanded = false
		    }
		},
		this);
        this._viewNodes = null;
        this["doUpdate"]()
    },
    clearData: function () {
        this["loadData"]([])
    },
    setUrl: function ($) {
        this.url = $;
        this.load($)
    },
    getUrl: function () {
        return this.url
    },
    loadNode: function (C, $) {
        C = this["getNode"](C);
        if (!C) return;
        if (this.isLeaf(C)) return;
        var B = {};
        B[this.idField] = this["getItemValue"](C);
        var _ = this;
        _["addNodeCls"](C, "mini-tree-loading");
        var D = this._ajaxOption.async;
        this._ajaxOption.async = true;
        var A = new Date();
        this.Sc(B, C,
		function (B) {
		    var E = new Date() - A;
		    if (E < 60) E = 60 - E;
		    setTimeout(function () {
		        _._ajaxOption.async = D;
		        _["removeNodeCls"](C, "mini-tree-loading");
		        _.removeNodes(C[_.nodesField]);
		        if (B && B.length > 0) {
		            _.addNodes(B, C);
		            if ($ !== false) _["expandNode"](C, true);
		            else _["collapseNode"](C, true);
		            _.fire("loadnode")
		        } else {
		            delete C.isLeaf;
		            _.Qi(C)
		        }
		    },
			E)
		},
		function ($) {
		    _["removeNodeCls"](C, "mini-tree-loading")
		});
        this.ajaxAsync = false
    },
    _ajaxOption: {
        async: false,
        type: "get"
    },
    setAjaxOption: function ($) {
        mini.copyTo(this._ajaxOption, $)
    },
    getAjaxOption: function ($) {
        return this._ajaxOption
    },
    Sc: function (_, A, B, C) {
        var E = A == this.root,
		D = {
		    url: this.url,
		    async: this._ajaxOption.async,
		    type: this._ajaxOption.type,
		    params: _,
		    cancel: false,
		    node: A,
		    isRoot: E
		};
        this.fire("beforeload", D);
        if (D.cancel == true) return;
        if (A != this.root);
        var $ = this;
        this.AJy = jQuery.ajax({
            url: D.url,
            async: D.async,
            data: D.params,
            type: D.type,
            cache: false,
            dataType: "text",
            success: function (D, C, _) {
                var F = null;
                try {
                    F = mini.decode(D)
                } catch (G) {
                    F = [];
                    throw new Error("tree json is error!")
                }
                var G = {
                    result: F,
                    data: F,
                    cancel: false,
                    node: A
                };
                if ($["resultAsTree"] == false) G.data = mini.arrayToTree(G.data, $.nodesField, $.idField, $["parentField"]);
                $.fire("preload", G);
                if (G.cancel == true) return;
                if (E) $["setData"](G.data);
                if (B) B(G.data);
                $.fire("load", G)
            },
            error: function (_, B, A) {
                var D = {
                    xmlHttp: _,
                    errorCode: B
                };
                if (C) C(D);
                $.fire("loaderror", D)
            }
        })
    },
    getItemValue: function ($) {
        if (!$) return "";
        var _ = $[this.idField];
        return mini.isNull(_) ? "" : String(_)
    },
    getItemText: function ($) {
        if (!$) return "";
        var _ = $[this.textField];
        return mini.isNull(_) ? "" : String(_)
    },
    KQl: function ($) {
        var B = this["showCheckBox"];
        if (B && this.hasChildren($)) B = this["showFolderCheckBox"];
        var _ = this["getItemText"]($),
		A = {
		    isLeaf: this.isLeaf($),
		    node: $,
		    nodeHtml: _,
		    nodeCls: "",
		    nodeStyle: "",
		    showCheckBox: B,
		    iconCls: this.getNodeIcon($),
		    showTreeIcon: this.showTreeIcon
		};
        this.fire("drawnode", A);
        if (A.nodeHtml === null || A.nodeHtml === undefined || A.nodeHtml === "") A.nodeHtml = "&nbsp;";
        return A
    },
    XS9: function (D, P, H) {
        var O = !H;
        if (!H) H = [];
        var K = D[this.textField];
        if (K === null || K === undefined) K = "";
        var M = this.isLeaf(D),
		$ = this.getLevel(D),
		Q = this.KQl(D),
		E = Q.nodeCls;
        if (!M) E = this.isExpandedNode(D) ? this.IpN : this.CA6;
        if (this.PI3 == D) E += " " + this.U4bQ;
        var F = this["getChildNodes"](D),
		I = F && F.length > 0;
        H[H.length] = "<div class=\"mini-tree-nodetitle " + E + "\" style=\"" + Q.nodeStyle + "\">";
        var A = this["getParentNode"](D),
		_ = 0;
        for (var J = _; J <= $; J++) {
            if (J == $) continue;
            if (M) if (this["showExpandButtons"] == false && J >= $ - 1) continue;
            var L = "";
            if (this._isInViewLastNode(D, J)) L = "background:none";
            H[H.length] = "<span class=\"mini-tree-indent \" style=\"" + L + "\"></span>"
        }
        var C = "";
        if (this._isViewFirstNode(D)) C = "mini-tree-node-ecicon-first";
        else if (this._isViewLastNode(D)) C = "mini-tree-node-ecicon-last";
        if (this._isViewFirstNode(D) && this._isViewLastNode(D)) C = "mini-tree-node-ecicon-last";
        if (!M) H[H.length] = "<a class=\"" + this.SR + " " + C + "\" style=\"" + (this["showExpandButtons"] ? "" : "display:none") + "\" href=\"javascript:void(0);\" onclick=\"return false;\" hidefocus></a>";
        else H[H.length] = "<span class=\"" + this.SR + " " + C + "\" ></span>";
        H[H.length] = "<span class=\"mini-tree-nodeshow\">";
        if (Q["showTreeIcon"]) H[H.length] = "<span class=\"" + Q.iconCls + " mini-tree-icon\"></span>";
        if (Q["showCheckBox"]) {
            var G = this.$n1A(D),
			N = this.isCheckedNode(D);
            H[H.length] = "<input type=\"checkbox\" id=\"" + G + "\" class=\"" + this.$Sn + "\" hidefocus " + (N ? "checked" : "") + "/>"
        }
        H[H.length] = "<span class=\"mini-tree-nodetext\">";
        if (P) {
            var B = this.uid + "$edit$" + D._id,
			K = D[this.textField];
            if (K === null || K === undefined) K = "";
            H[H.length] = "<input id=\"" + B + "\" type=\"text\" class=\"mini-tree-editinput\" value=\"" + K + "\"/>"
        } else H[H.length] = Q.nodeHtml;
        H[H.length] = "</span>";
        H[H.length] = "</span>";
        H[H.length] = "</div>";
        if (O) return H.join("")
    },
    NCA: function (A, D) {
        var C = !D;
        if (!D) D = [];
        if (!A) return "";
        var _ = this.GbUs(A),
		$ = this.isVisibleNode(A) ? "" : "display:none";
        D[D.length] = "<div id=\"";
        D[D.length] = _;
        D[D.length] = "\" class=\"";
        D[D.length] = this.ME$h;
        D[D.length] = "\" style=\"";
        D[D.length] = $;
        D[D.length] = "\">";
        this.XS9(A, false, D);
        var B = this._getViewChildNodes(A);
        if (B) if (this.removeOnCollapse && this.isExpandedNode(A)) this.UhvM(B, A, D);
        D[D.length] = "</div>";
        if (C) return D.join("")
    },
    UhvM: function (F, B, G) {
        var E = !G;
        if (!G) G = [];
        if (!F) return "";
        var C = this.TC(B),
		$ = this.isExpandedNode(B) ? "" : "display:none";
        G[G.length] = "<div id=\"";
        G[G.length] = C;
        G[G.length] = "\" class=\"";
        G[G.length] = this.Xu;
        G[G.length] = "\" style=\"";
        G[G.length] = $;
        G[G.length] = "\">";
        for (var _ = 0, D = F.length; _ < D; _++) {
            var A = F[_];
            this.NCA(A, G)
        }
        G[G.length] = "</div>";
        if (E) return G.join("")
    },
    doUpdate: function () {
        if (!this.N2b) return;
        var $ = this._getViewChildNodes(this.root),
		A = [];
        this.UhvM($, this.root, A);
        var _ = A.join("");
        this.$kJ.innerHTML = _;
        this.Mso()
    },
    VYt: function () { },
    Mso: function () {
        var $ = this;
        if (this.JIEI) return;
        this.JIEI = setTimeout(function () {
            $["doLayout"]();
            $.JIEI = null
        },
		1)
    },
    doLayout: function () {
        if (this["showCheckBox"]) RE(this.el, "mini-tree-showCheckBox");
        else WeL(this.el, "mini-tree-showCheckBox");
        if (this["enableHotTrack"]) RE(this.el, "mini-tree-hottrack");
        else WeL(this.el, "mini-tree-hottrack");
        var $ = this.el.firstChild;
        if ($) RE($, "mini-tree-rootnodes")
    },
    filter: function (C, B) {
        B = B || this;
        var A = this._viewNodes = {},
		_ = this.nodesField;
        function $(G) {
            var J = G[_];
            if (!J) return false;
            var K = G._id,
			H = [];
            for (var D = 0, I = J.length; D < I; D++) {
                var F = J[D],
				L = $(F),
				E = C["call"](B, F, D, this);
                if (E === true || L) H.push(F)
            }
            if (H.length > 0) A[K] = H;
            return H.length > 0
        }
        $(this.root);
        this["doUpdate"]()
    },
    clearFilter: function () {
        if (this._viewNodes) {
            this._viewNodes = null;
            this["doUpdate"]()
        }
    },
    setShowCheckBox: function ($) {
        if (this["showCheckBox"] != $) {
            this["showCheckBox"] = $;
            this["doUpdate"]()
        }
    },
    getShowCheckBox: function () {
        return this["showCheckBox"]
    },
    setShowFolderCheckBox: function ($) {
        if (this["showFolderCheckBox"] != $) {
            this["showFolderCheckBox"] = $;
            this["doUpdate"]()
        }
    },
    getShowFolderCheckBox: function () {
        return this["showFolderCheckBox"]
    },
    setAllowSelect: function ($) {
        if (this["allowSelect"] != $) {
            this["allowSelect"] = $;
            this["doUpdate"]()
        }
    },
    getAllowSelect: function () {
        return this["allowSelect"]
    },
    setShowTreeIcon: function ($) {
        if (this["showTreeIcon"] != $) {
            this["showTreeIcon"] = $;
            this["doUpdate"]()
        }
    },
    getShowTreeIcon: function () {
        return this["showTreeIcon"]
    },
    setShowExpandButtons: function ($) {
        if (this["showExpandButtons"] != $) {
            this["showExpandButtons"] = $;
            this["doUpdate"]()
        }
    },
    getShowExpandButtons: function () {
        return this["showExpandButtons"]
    },
    setEnableHotTrack: function ($) {
        if (this["enableHotTrack"] != $) {
            this["enableHotTrack"] = $;
            this["doLayout"]()
        }
    },
    getEnableHotTrack: function () {
        return this["enableHotTrack"]
    },
    setExpandOnLoad: function ($) {
        this.expandOnLoad = $
    },
    getExpandOnLoad: function () {
        return this.expandOnLoad
    },
    setCheckRecursive: function ($) {
        if (this["checkRecursive"] != $) this["checkRecursive"] = $
    },
    getCheckRecursive: function () {
        return this["checkRecursive"]
    },
    getNodeIcon: function (_) {
        var $ = _[this.iconField];
        if (!$) if (this.isLeaf(_)) $ = this.leafIcon;
        else $ = this.folderIcon;
        return $
    },
    isAncestor: function (_, B) {
        if (_ == B) return true;
        if (!_ || !B) return false;
        var A = this["getAncestors"](B);
        for (var $ = 0, C = A.length; $ < C; $++) if (A[$] == _) return true;
        return false
    },
    getAncestors: function (A) {
        var _ = [];
        while (1) {
            var $ = this["getParentNode"](A);
            if (!$ || $ == this.root) break;
            _[_.length] = $;
            A = $
        }
        _.reverse();
        return _
    },
    getRootNode: function () {
        return this.root
    },
    getParentNode: function ($) {
        if (!$) return null;
        if ($._pid == this.root._id) return this.root;
        return this.C4G[$._pid]
    },
    _isViewFirstNode: function (_) {
        if (this._viewNodes) {
            var $ = this["getParentNode"](_),
			A = this._getViewChildNodes($);
            return A[0] === _
        } else return this["isFirstNode"](_)
    },
    _isViewLastNode: function (_) {
        if (this._viewNodes) {
            var $ = this["getParentNode"](_),
			A = this._getViewChildNodes($);
            return A[A.length - 1] === _
        } else return this.isLastNode(_)
    },
    _isInViewLastNode: function (D, $) {
        if (this._viewNodes) {
            var C = null,
			A = this["getAncestors"](D);
            for (var _ = 0, E = A.length; _ < E; _++) {
                var B = A[_];
                if (this.getLevel(B) == $) C = B
            }
            if (!C || C == this.root) return false;
            return this._isViewLastNode(C)
        } else return this.isInLastNode(D, $)
    },
    _getViewChildNodes: function ($) {
        if (this._viewNodes) return this._viewNodes[$._id];
        else return this["getChildNodes"]($)
    },
    getChildNodes: function ($) {
        $ = this["getNode"]($);
        if (!$) return null;
        return $[this.nodesField]
    },
    getAllChildNodes: function ($) {
        $ = this["getNode"]($);
        if (!$) return [];
        var _ = [];
        this.cascadeChild($,
		function ($) {
		    _.push($)
		},
		this);
        return _
    },
    indexOf: function (_) {
        _ = this["getNode"](_);
        if (!_) return -1;
        this.getList();
        var $ = this._indexs[_[this.idField]];
        if (mini.isNull($)) return -1;
        return $
    },
    getAt: function (_) {
        var $ = this.getList();
        return $[_]
    },
    indexOfChildren: function (A) {
        var $ = this["getParentNode"](A);
        if (!$) return -1;
        var _ = $[this.nodesField];
        return _.indexOf(A)
    },
    hasChildren: function ($) {
        var _ = this["getChildNodes"]($);
        return !!(_ && _.length > 0)
    },
    isLeaf: function ($) {
        if (!$ || $.isLeaf === false) return false;
        var _ = this["getChildNodes"]($);
        if (_ && _.length > 0) return false;
        return true
    },
    getLevel: function ($) {
        return $._level
    },
    isExpandedNode: function ($) {
        $ = this["getNode"]($);
        if (!$) return false;
        return $.expanded == true || mini.isNull($.expanded)
    },
    isCheckedNode: function ($) {
        return $.checked == true
    },
    isVisibleNode: function ($) {
        return $.visible !== false
    },
    isEnabledNode: function ($) {
        return $.enabled !== false || this.enabled
    },
    isFirstNode: function (_) {
        var $ = this["getParentNode"](_),
		A = this["getChildNodes"]($);
        return A[0] === _
    },
    isLastNode: function (_) {
        var $ = this["getParentNode"](_),
		A = this["getChildNodes"]($);
        return A[A.length - 1] === _
    },
    isInLastNode: function (D, $) {
        var C = null,
		A = this["getAncestors"](D);
        for (var _ = 0, E = A.length; _ < E; _++) {
            var B = A[_];
            if (this.getLevel(B) == $) C = B
        }
        if (!C || C == this.root) return false;
        return this.isLastNode(C)
    },
    bubbleParent: function (_, B, A) {
        A = A || this;
        if (_) B["call"](this, _);
        var $ = this["getParentNode"](_);
        if ($ && $ != this.root) this.bubbleParent($, B, A)
    },
    cascadeChild: function (A, E, B) {
        if (!E) return;
        if (!A) A = this.root;
        var D = A[this.nodesField];
        if (D) {
            D = D.clone();
            for (var $ = 0, C = D.length; $ < C; $++) {
                var _ = D[$];
                if (E["call"](B || this, _, $, A) === false) return;
                this.cascadeChild(_, E, B)
            }
        }
    },
    eachChild: function (B, F, C) {
        if (!F || !B) return;
        var E = B[this.nodesField];
        if (E) {
            var _ = E.clone();
            for (var A = 0, D = _.length; A < D; A++) {
                var $ = _[A];
                if (F["call"](C || this, $, A, B) === false) break
            }
        }
    },
    RFtO: function (_, $) {
        if (!_._id) _._id = mini.Tree.NodeUID++;
        this.C4G[_._id] = _;
        this.NiWt[_[this.idField]] = _;
        _._pid = $ ? $._id : "";
        _._level = $ ? $._level + 1 : -1;
        this.cascadeChild(_,
		function (A, $, _) {
		    if (!A._id) A._id = mini.Tree.NodeUID++;
		    this.C4G[A._id] = A;
		    this.NiWt[A[this.idField]] = A;
		    A._pid = _._id;
		    A._level = _._level + 1
		},
		this);
        this._clearTree()
    },
    Bi$: function (_) {
        var $ = this;
        function A(_) {
            $.Qi(_)
        }
        if (_ != this.root) A(_);
        this.cascadeChild(_,
		function ($) {
		    A($)
		},
		this)
    },
    removeNodes: function (B) {
        if (!mini.isArray(B)) return;
        B = B.clone();
        for (var $ = 0, A = B.length; $ < A; $++) {
            var _ = B[$];
            this["removeNode"](_)
        }
    },
    Qi: function ($) {
        var A = this.XS9($),
		_ = this._getNodeEl($);
        if (_) jQuery(_.firstChild).replaceWith(A)
    },
    setNodeText: function (_, $) {
        _ = this["getNode"](_);
        if (!_) return;
        _[this.textField] = $;
        this.Qi(_)
    },
    setNodeIconCls: function (_, $) {
        _ = this["getNode"](_);
        if (!_) return;
        _[this.iconField] = $;
        this.Qi(_)
    },
    updateNode: function (_, $) {
        _ = this["getNode"](_);
        if (!_ || !$) return;
        var A = _[this.nodesField];
        mini.copyTo(_, $);
        _[this.nodesField] = A;
        this.Qi(_)
    },
    removeNode: function (A) {
        A = this["getNode"](A);
        if (!A) return;
        if (this.PI3 == A) this.PI3 = null;
        var D = [A];
        this.cascadeChild(A,
		function ($) {
		    D.push($)
		},
		this);
        var _ = this["getParentNode"](A);
        _[this.nodesField].remove(A);
        this.RFtO(A, _);
        var B = this._getNodeEl(A);
        if (B) B.parentNode.removeChild(B);
        this.Bi$(_);
        for (var $ = 0, C = D.length; $ < C; $++) {
            var A = D[$];
            delete A._id;
            delete A._pid;
            delete this.C4G[A._id];
            delete this.NiWt[A[this.idField]]
        }
    },
    addNodes: function (D, _, A) {
        if (!mini.isArray(D)) return;
        for (var $ = 0, C = D.length; $ < C; $++) {
            var B = D[$];
            this.addNode(B, A, _)
        }
    },
    addNode: function (C, $, _) {
        C = this["getNode"](C);
        if (!C) return;
        if (!_) $ = "add";
        var B = _;
        switch ($) {
            case "before":
                if (!B) return;
                _ = this["getParentNode"](B);
                var A = _[this.nodesField];
                $ = A.indexOf(B);
                break;
            case "after":
                if (!B) return;
                _ = this["getParentNode"](B);
                A = _[this.nodesField];
                $ = A.indexOf(B) + 1;
                break;
            case "add":
                break;
            default:
                break
        }
        _ = this["getNode"](_);
        if (!_) _ = this.root;
        var F = _[this.nodesField];
        if (!F) F = _[this.nodesField] = [];
        $ = parseInt($);
        if (isNaN($)) $ = F.length;
        B = F[$];
        if (!B) $ = F.length;
        F.insert($, C);
        this.RFtO(C, _);
        var E = this.CXs(_);
        if (E) {
            var H = this.NCA(C),
			$ = F.indexOf(C) + 1,
			B = F[$];
            if (B) {
                var G = this._getNodeEl(B);
                jQuery(G).before(H)
            } else mini.append(E, H)
        } else {
            var H = this.NCA(_),
			D = this._getNodeEl(_);
            jQuery(D).replaceWith(H)
        }
        _ = this["getParentNode"](C);
        this.Bi$(_)
    },
    moveNodes: function (E, B, _) {
        if (!E || E.length == 0 || !B || !_) return;
        this.beginUpdate();
        var A = this;
        for (var $ = 0, D = E.length; $ < D; $++) {
            var C = E[$];
            this.moveNode(C, B, _);
            if ($ != 0) {
                B = C;
                _ = "after"
            }
        }
        this.endUpdate()
    },
    moveNode: function (G, E, C) {
        G = this["getNode"](G);
        E = this["getNode"](E);
        if (!G || !E || !C) return false;
        if (this.isAncestor(G, E)) return false;
        var $ = -1,
		_ = null;
        switch (C) {
            case "before":
                _ = this["getParentNode"](E);
                $ = this.indexOfChildren(E);
                break;
            case "after":
                _ = this["getParentNode"](E);
                $ = this.indexOfChildren(E) + 1;
                break;
            default:
                _ = E;
                var B = this["getChildNodes"](_);
                if (!B) B = _[this.nodesField] = [];
                $ = B.length;
                break
        }
        var F = {},
		B = this["getChildNodes"](_);
        B.insert($, F);
        var D = this["getParentNode"](G),
		A = this["getChildNodes"](D);
        A.remove(G);
        $ = B.indexOf(F);
        B[$] = G;
        this.RFtO(G, _);
        this["doUpdate"]();
        return true
    },
    isEditingNode: function ($) {
        return this._editingNode == $
    },
    beginEdit: function (_) {
        _ = this["getNode"](_);
        if (!_) return;
        var A = this._getNodeEl(_),
		B = this.XS9(_, true),
		A = this._getNodeEl(_);
        if (A) jQuery(A.firstChild).replaceWith(B);
        this._editingNode = _;
        var $ = this.uid + "$edit$" + _._id;
        this._editInput = document.getElementById($);
        this._editInput.focus();
        mini.selectRange(this._editInput, 1000, 1000);
        VNV(this._editInput, "keydown", this.OBkC, this);
        VNV(this._editInput, "blur", this.AmQ, this)
    },
    cancelEdit: function () {
        if (this._editingNode) {
            this.Qi(this._editingNode);
            BP(this._editInput, "keydown", this.OBkC, this);
            BP(this._editInput, "blur", this.AmQ, this)
        }
        this._editingNode = null;
        this._editInput = null
    },
    OBkC: function (_) {
        if (_.keyCode == 13) {
            var $ = this._editInput.value;
            this.setNodeText(this._editingNode, $);
            this["cancelEdit"]()
        } else if (_.keyCode == 27) this["cancelEdit"]()
    },
    AmQ: function (_) {
        var $ = this._editInput.value;
        this.setNodeText(this._editingNode, $);
        this["cancelEdit"]()
    },
    _getNodeByEvent: function (C) {
        if (D4ge(C.target, this.Xu)) return null;
        var A = MRj9(C.target, this.ME$h);
        if (A) {
            var $ = A.id.split("$"),
			B = $[$.length - 1],
			_ = this.C4G[B];
            return _
        }
        return null
    },
    GbUs: function ($) {
        return this.uid + "$" + $._id
    },
    TC: function ($) {
        return this.uid + "$nodes$" + $._id
    },
    $n1A: function ($) {
        return this.uid + "$check$" + $._id
    },
    addNodeCls: function ($, _) {
        var A = this._getNodeEl($);
        if (A) RE(A, _)
    },
    removeNodeCls: function ($, _) {
        var A = this._getNodeEl($);
        if (A) WeL(A, _)
    },
    getNodeBox: function (_) {
        var $ = this._getNodeEl(_);
        if ($) return EcG($.firstChild)
    },
    _getNodeEl: function ($) {
        if (!$) return null;
        var _ = this.GbUs($);
        return document.getElementById(_)
    },
    EYQR: function (_) {
        if (!_) return null;
        var $ = this.Ro(_);
        if ($) {
            $ = mini.byClass(this.WnSi, $);
            return $
        }
        return null
    },
    Ro: function (_) {
        var $ = this._getNodeEl(_);
        if ($) return $.firstChild
    },
    CXs: function ($) {
        if (!$) return null;
        var _ = this.TC($);
        return document.getElementById(_)
    },
    R0Cg: function ($) {
        if (!$) return null;
        var _ = this.$n1A($);
        return document.getElementById(_)
    },
    findNodes: function (A, $) {
        var _ = [];
        $ = $ || this;
        this.cascadeChild(this.root,
		function (B) {
		    if (A && A["call"]($, B) === true) _.push(B)
		},
		this);
        return _
    },
    getNode: function ($) {
        if (typeof $ == "object") return $;
        return this.NiWt[$] || null
    },
    hideNode: function (_) {
        _ = this["getNode"](_);
        if (!_) return;
        _.visible = false;
        var $ = this._getNodeEl(_);
        $.style.display = "none"
    },
    showNode: function (_) {
        _ = this["getNode"](_);
        if (!_) return;
        _.visible = false;
        var $ = this._getNodeEl(_);
        $.style.display = ""
    },
    enableNode: function (A) {
        A = this["getNode"](A);
        if (!A) return;
        A.enabled = true;
        var _ = this._getNodeEl(A);
        WeL(_, "mini-disabled");
        var $ = this.R0Cg(A);
        if ($) $.disabled = false
    },
    disableNode: function (A) {
        A = this["getNode"](A);
        if (!A) return;
        A.enabled = false;
        var _ = this._getNodeEl(A);
        RE(_, "mini-disabled");
        var $ = this.R0Cg(A);
        if ($) $.disabled = true
    },
    _allowExpandLayout: true,
    expandNode: function (E, B) {
        E = this["getNode"](E);
        if (!E) return;
        var $ = this.isExpandedNode(E);
        if ($) return;
        if (this.isLeaf(E)) return;
        E.expanded = true;
        var F = this._getNodeEl(E);
        if (this.removeOnCollapse && F) {
            var G = this.NCA(E);
            jQuery(F).before(G);
            jQuery(F).remove()
        }
        var D = this.CXs(E);
        if (D) D.style.display = "";
        D = this._getNodeEl(E);
        if (D) {
            var I = D.firstChild;
            WeL(I, this.CA6);
            RE(I, this.IpN)
        }
        this.fire("expand", {
            node: E
        });
        B = B && !(mini.isIE6);
        if (B && this._getViewChildNodes(E)) {
            this._g = true;
            D = this.CXs(E);
            if (!D) return;
            var C = Nf(D);
            D.style.height = "1px";
            if (this.UkA) D.style.position = "relative";
            var _ = {
                height: C + "px"
            },
			A = this,
			H = jQuery(D);
            H.animate(_, 180,
			function () {
			    A._g = false;
			    A.VYt();
			    clearInterval(A.FAn);
			    D.style.height = "auto";
			    if (A.UkA) D.style.position = "static";
			    mini["repaint"](F)
			});
            clearInterval(this.FAn);
            this.FAn = setInterval(function () {
                A.VYt()
            },
			60)
        }
        this.VYt();
        if (this._allowExpandLayout) mini["repaint"](this.el)
    },
    collapseNode: function (E, B) {
        E = this["getNode"](E);
        if (!E) return;
        var $ = this.isExpandedNode(E);
        if (!$) return;
        if (this.isLeaf(E)) return;
        E.expanded = false;
        var F = this._getNodeEl(E),
		D = this.CXs(E);
        if (D) D.style.display = "none";
        D = this._getNodeEl(E);
        if (D) {
            var I = D.firstChild;
            WeL(I, this.IpN);
            RE(I, this.CA6)
        }
        this.fire("collapse", {
            node: E
        });
        B = B && !(mini.isIE6);
        if (B && this._getViewChildNodes(E)) {
            this._g = true;
            D = this.CXs(E);
            if (!D) return;
            D.style.display = "";
            D.style.height = "auto";
            if (this.UkA) D.style.position = "relative";
            var C = Nf(D),
			_ = {
			    height: "1px"
			},
			A = this,
			H = jQuery(D);
            H.animate(_, 180,
			function () {
			    D.style.display = "none";
			    D.style.height = "auto";
			    if (A.UkA) D.style.position = "static";
			    A._g = false;
			    A.VYt();
			    clearInterval(A.FAn);
			    var $ = A.CXs(E);
			    if (A.removeOnCollapse && $) jQuery($).remove();
			    mini["repaint"](F)
			});
            clearInterval(this.FAn);
            this.FAn = setInterval(function () {
                A.VYt()
            },
			60)
        } else {
            var G = this.CXs(E);
            if (this.removeOnCollapse && G) jQuery(G).remove()
        }
        this.VYt();
        if (this._allowExpandLayout) mini["repaint"](this.el)
    },
    toggleNode: function (_, $) {
        if (this.isExpandedNode(_)) this["collapseNode"](_, $);
        else this["expandNode"](_, $)
    },
    expandLevel: function ($) {
        this.cascadeChild(this.root,
		function (_) {
		    if (this.getLevel(_) == $) if (_[this.nodesField] != null) this["expandNode"](_)
		},
		this)
    },
    collapseLevel: function ($) {
        this.cascadeChild(this.root,
		function (_) {
		    if (this.getLevel(_) == $) if (_[this.nodesField] != null) this["collapseNode"](_)
		},
		this)
    },
    expandAll: function () {
        this.cascadeChild(this.root,
		function ($) {
		    if ($[this.nodesField] != null) this["expandNode"]($)
		},
		this)
    },
    collapseAll: function () {
        this.cascadeChild(this.root,
		function ($) {
		    if ($[this.nodesField] != null) this["collapseNode"]($)
		},
		this)
    },
    expandPath: function (A) {
        A = this["getNode"](A);
        if (!A) return;
        var _ = this["getAncestors"](A);
        for (var $ = 0, B = _.length; $ < B; $++) this["expandNode"](_[$])
    },
    collapsePath: function (A) {
        A = this["getNode"](A);
        if (!A) return;
        var _ = this["getAncestors"](A);
        for (var $ = 0, B = _.length; $ < B; $++) this["collapseNode"](_[$])
    },
    selectNode: function (_) {
        _ = this["getNode"](_);
        var $ = this._getNodeEl(this.PI3);
        if ($) WeL($.firstChild, this.U4bQ);
        this.PI3 = _;
        $ = this._getNodeEl(this.PI3);
        if ($) RE($.firstChild, this.U4bQ);
        var A = {
            node: _,
            isLeaf: this.isLeaf(_)
        };
        this.fire("nodeselect", A)
    },
    getSelectedNode: function () {
        return this.PI3
    },
    getSelectedNodes: function () {
        var $ = [];
        if (this.PI3) $.push(this.PI3);
        return $
    },
    autoCheckParent: false,
    setAutoCheckParent: function ($) {
        this.autoCheckParent = $
    },
    getAutoCheckParent: function ($) {
        return this.autoCheckParent
    },
    doAutoCheckParent: function (C) {
        var _ = this["getAncestors"](C);
        for (var $ = 0, D = _.length; $ < D; $++) {
            var B = _[$],
			A = this.hasCheckedChildNode(B);
            B.checked = A;
            var E = this.R0Cg(B);
            if (E) {
                E.indeterminate = false;
                E.checked = A
            }
        }
    },
    hasCheckedChildNode: function (_) {
        var A = false,
		D = this.getAllChildNodes(_);
        for (var $ = 0, C = D.length; $ < C; $++) {
            var B = D[$];
            if (this.isCheckedNode(B)) {
                A = true;
                break
            }
        }
        return A
    },
    _doCheckState: function (C) {
        var _ = this["getAncestors"](C);
        _.push(C);
        for (var $ = 0, D = _.length; $ < D; $++) {
            var B = _[$],
			A = this.hasCheckedChildNode(B),
			E = this.R0Cg(B);
            if (E) {
                E.indeterminate = false;
                if (this.isCheckedNode(B)) {
                    E.indeterminate = false;
                    E.checked = true
                } else {
                    E.indeterminate = A;
                    E.checked = false
                }
            }
        }
    },
    checkNode: function ($) {
        $ = this["getNode"]($);
        if (!$ || $.checked) return;
        $.checked = true;
        this._doCheckState($)
    },
    uncheckNode: function ($) {
        $ = this["getNode"]($);
        if (!$ || !$.checked) return;
        $.checked = false;
        this._doCheckState($)
    },
    checkNodes: function (B) {
        if (!mini.isArray(B)) B = [];
        for (var $ = 0, A = B.length; $ < A; $++) {
            var _ = B[$];
            this.checkNode(_)
        }
    },
    uncheckNodes: function (B) {
        if (!mini.isArray(B)) B = [];
        for (var $ = 0, A = B.length; $ < A; $++) {
            var _ = B[$];
            this.uncheckNode(_)
        }
    },
    checkAllNodes: function () {
        this.cascadeChild(this.root,
		function ($) {
		    this.checkNode($)
		},
		this)
    },
    uncheckAllNodes: function ($) {
        this.cascadeChild(this.root,
		function ($) {
		    this.uncheckNode($)
		},
		this)
    },
    getCheckedNodes: function () {
        var $ = [];
        this.cascadeChild(this.root,
		function (_) {
		    if (_.checked == true) $.push(_)
		},
		this);
        return $
    },
    setValue: function (_) {
        if (mini.isNull(_)) _ = "";
        _ = String(_);
        if (this.getValue() != _) {
            var C = this.getCheckedNodes();
            this.uncheckNodes(C);
            this.value = _;
            var A = String(_).split(",");
            for (var $ = 0, B = A.length; $ < B; $++) this.checkNode(A[$])
        }
    },
    getNodesByValue: function (_) {
        if (mini.isNull(_)) _ = "";
        _ = String(_);
        var D = [],
		A = String(_).split(",");
        for (var $ = 0, C = A.length; $ < C; $++) {
            var B = this["getNode"](A[$]);
            if (B) D.push(B)
        }
        return D
    },
    B0$Z: function (A) {
        if (mini.isNull(A)) A = [];
        if (!mini.isArray(A)) A = this.getNodesByValue(A);
        var B = [],
		C = [];
        for (var _ = 0, D = A.length; _ < D; _++) {
            var $ = A[_];
            if ($) {
                B.push(this["getItemValue"]($));
                C.push(this["getItemText"]($))
            }
        }
        return [B.join(this.delimiter), C.join(this.delimiter)]
    },
    getValue: function () {
        var A = this.getCheckedNodes(),
		C = [];
        for (var $ = 0, _ = A.length; $ < _; $++) {
            var B = this["getItemValue"](A[$]);
            if (B) C.push(B)
        }
        return C.join(",")
    },
    setResultAsTree: function ($) {
        this["resultAsTree"] = $
    },
    getResultAsTree: function () {
        return this["resultAsTree"]
    },
    setParentField: function ($) {
        this["parentField"] = $
    },
    getParentField: function () {
        return this["parentField"]
    },
    setIdField: function ($) {
        this["idField"] = $
    },
    getIdField: function () {
        return this["idField"]
    },
    setTextField: function ($) {
        this["textField"] = $
    },
    getTextField: function () {
        return this["textField"]
    },
    setShowTreeLines: function ($) {
        this["showTreeLines"] = $;
        if ($ == true) RE(this.el, "mini-tree-treeLine");
        else WeL(this.el, "mini-tree-treeLine")
    },
    getShowTreeLines: function () {
        return this["showTreeLines"]
    },
    setShowArrow: function ($) {
        this.showArrow = $;
        if ($ == true) RE(this.el, "mini-tree-showArrows");
        else WeL(this.el, "mini-tree-showArrows")
    },
    getShowArrow: function () {
        return this.showArrow
    },
    setIconField: function ($) {
        this.iconField = $
    },
    getIconField: function () {
        return this.iconField
    },
    setNodesField: function ($) {
        this.nodesField = $
    },
    getNodesField: function () {
        return this.nodesField
    },
    setTreeColumn: function ($) {
        this.treeColumn = $
    },
    getTreeColumn: function () {
        return this.treeColumn
    },
    setLeafIcon: function ($) {
        this.leafIcon = $
    },
    getLeafIcon: function () {
        return this.leafIcon
    },
    setFolderIcon: function ($) {
        this.folderIcon = $
    },
    getFolderIcon: function () {
        return this.folderIcon
    },
    setExpandOnDblClick: function ($) {
        this.expandOnDblClick = $
    },
    getExpandOnDblClick: function () {
        return this.expandOnDblClick
    },
    setRemoveOnCollapse: function ($) {
        this.removeOnCollapse = $
    },
    getRemoveOnCollapse: function () {
        return this.removeOnCollapse
    },
    E8S: function (B) {
        if (!this.enabled) return;
        if (MRj9(B.target, this.$Sn)) return;
        var $ = this._getNodeByEvent(B);
        if ($) if (MRj9(B.target, this.WnSi)) {
            var _ = this.isExpandedNode($),
			A = {
			    node: $,
			    expanded: _,
			    cancel: false
			};
            if (this.expandOnDblClick && !this._g) if (_) {
                this.fire("beforecollapse", A);
                if (A.cancel == true) return;
                this["collapseNode"]($, this.allowAnim)
            } else {
                this.fire("beforeexpand", A);
                if (A.cancel == true) return;
                this["expandNode"]($, this.allowAnim)
            }
            this.fire("nodedblclick", {
                htmlEvent: B,
                node: $
            })
        }
    },
    YY: function (L) {
        if (!this.enabled) return;
        var B = this._getNodeByEvent(L);
        if (B) if (MRj9(L.target, this.SR) && this.isLeaf(B) == false) {
            if (this._g) return;
            var I = this.isExpandedNode(B),
			K = {
			    node: B,
			    expanded: I,
			    cancel: false
			};
            if (!this._g) if (I) {
                this.fire("beforecollapse", K);
                if (K.cancel == true) return;
                this["collapseNode"](B, this.allowAnim)
            } else {
                this.fire("beforeexpand", K);
                if (K.cancel == true) return;
                this["expandNode"](B, this.allowAnim)
            }
        } else if (MRj9(L.target, this.$Sn)) {
            var J = this.isCheckedNode(B),
			K = {
			    isLeaf: this.isLeaf(B),
			    node: B,
			    checked: J,
			    checkRecursive: this.checkRecursive,
			    htmlEvent: L,
			    cancel: false
			};
            this.fire("beforenodecheck", K);
            if (K.cancel == true) {
                L.preventDefault();
                return
            }
            if (J) this.uncheckNode(B);
            else this.checkNode(B);
            if (K["checkRecursive"]) {
                this.cascadeChild(B,
				function ($) {
				    if (J) this.uncheckNode($);
				    else this.checkNode($)
				},
				this);
                var $ = this["getAncestors"](B);
                $.reverse();
                for (var G = 0, F = $.length; G < F; G++) {
                    var C = $[G],
					A = this["getChildNodes"](C),
					H = true;
                    for (var _ = 0, E = A.length; _ < E; _++) {
                        var D = A[_];
                        if (!this.isCheckedNode(D)) {
                            H = false;
                            break
                        }
                    }
                    if (H) this.checkNode(C);
                    else this.uncheckNode(C)
                }
            }
            if (this.autoCheckParent) this.doAutoCheckParent(B);
            this.fire("nodecheck", K)
        } else this._OnNodeClick(B, L)
    },
    UNAE: function (_) {
        if (!this.enabled) return;
        var $ = this._getNodeByEvent(_);
        if ($) if (MRj9(_.target, this.SR));
        else if (MRj9(_.target, this.$Sn));
        else this._OnNodeMouseDown($, _)
    },
    _OnNodeMouseDown: function (_, $) {
        var B = MRj9($.target, this.WnSi);
        if (!B) return null;
        if (!this.isEnabledNode(_)) return;
        var A = {
            node: _,
            cancel: false,
            isLeaf: this.isLeaf(_),
            htmlEvent: $
        };
        if (this["allowSelect"] && _["allowSelect"] !== false) if (this.PI3 != _) {
            this.fire("beforenodeselect", A);
            if (A.cancel != true) this.selectNode(_)
        }
        this.fire("nodeMouseDown", A)
    },
    _OnNodeClick: function (_, $) {
        var B = MRj9($.target, this.WnSi);
        if (!B) return null;
        if ($.target.tagName.toLowerCase() == "a") $.target.hideFocus = true;
        if (!this.isEnabledNode(_)) return;
        var A = {
            node: _,
            cancel: false,
            isLeaf: this.isLeaf(_),
            htmlEvent: $
        };
        this.fire("nodeClick", A)
    },
    A$Z: function (_) {
        var $ = this._getNodeByEvent(_);
        if ($) this._OnNodeMouseMove($, _)
    },
    Gj$: function (_) {
        var $ = this._getNodeByEvent(_);
        if ($) this._OnNodeMouseOut($, _)
    },
    _OnNodeMouseOut: function ($, _) {
        if (!this.isEnabledNode($)) return;
        if (!MRj9(_.target, this.WnSi)) return;
        this.blurNode();
        var _ = {
            node: $,
            htmlEvent: _
        };
        this.fire("nodemouseout", _)
    },
    _OnNodeMouseMove: function ($, _) {
        if (!this.isEnabledNode($)) return;
        if (!MRj9(_.target, this.WnSi)) return;
        if (this["enableHotTrack"] == true) this.focusNode($);
        var _ = {
            node: $,
            htmlEvent: _
        };
        this.fire("nodemousemove", _)
    },
    focusNode: function (A, $) {
        A = this["getNode"](A);
        if (!A) return;
        var _ = this.EYQR(A);
        if ($ && _) this["scrollIntoView"](A);
        if (this.Whut == A) return;
        this.blurNode();
        this.Whut = A;
        RE(_, this.UdPE)
    },
    blurNode: function () {
        if (!this.Whut) return;
        var $ = this.EYQR(this.Whut);
        if ($) WeL($, this.UdPE);
        this.Whut = null
    },
    scrollIntoView: function (_) {
        var $ = this._getNodeEl(_);
        mini["scrollIntoView"]($, this.el, false)
    },
    onNodeClick: function (_, $) {
        this.on("nodeClick", _, $)
    },
    onBeforeNodeSelect: function (_, $) {
        this.on("beforenodeselect", _, $)
    },
    onNodeSelect: function (_, $) {
        this.on("nodeselect", _, $)
    },
    onBeforeNodeCheck: function (_, $) {
        this.on("beforenodecheck", _, $)
    },
    onCheckNode: function (_, $) {
        this.on("nodecheck", _, $)
    },
    onNodeMouseDown: function (_, $) {
        this.on("nodemousedown", _, $)
    },
    onBeforeExpand: function (_, $) {
        this.on("beforeexpand", _, $)
    },
    onExpand: function (_, $) {
        this.on("expand", _, $)
    },
    onBeforeCollapse: function (_, $) {
        this.on("beforecollapse", _, $)
    },
    onCollapse: function (_, $) {
        this.on("collapse", _, $)
    },
    onBeforeLoad: function (_, $) {
        this.on("beforeload", _, $)
    },
    onLoad: function (_, $) {
        this.on("load", _, $)
    },
    onLoadError: function (_, $) {
        this.on("loaderror", _, $)
    },
    onDataLoad: function (_, $) {
        this.on("dataload", _, $)
    },
    _FcData: function () {
        return this.getSelectedNodes().clone()
    },
    _FcText: function ($) {
        return "Nodes " + $.length
    },
    allowDrag: false,
    allowDrop: false,
    dragGroupName: "",
    dropGroupName: "",
    setAllowDrag: function ($) {
        this.allowDrag = $
    },
    getAllowDrag: function () {
        return this.allowDrag
    },
    setAllowDrop: function ($) {
        this["allowDrop"] = $
    },
    getAllowDrop: function () {
        return this["allowDrop"]
    },
    setDragGroupName: function ($) {
        this["dragGroupName"] = $
    },
    getDragGroupName: function () {
        return this["dragGroupName"]
    },
    setDropGroupName: function ($) {
        this["dropGroupName"] = $
    },
    getDropGroupName: function () {
        return this["dropGroupName"]
    },
    isAllowDrag: function ($) {
        if (!this.allowDrag) return false;
        if ($.allowDrag === false) return false;
        var _ = this.J_sA($);
        return !_.cancel
    },
    J_sA: function ($) {
        var _ = {
            node: $,
            cancel: false
        };
        this.fire("DragStart", _);
        return _
    },
    Pll6: function (_, $, A) {
        _ = _.clone();
        var B = {
            dragNodes: _,
            targetNode: $,
            action: A,
            cancel: false
        };
        this.fire("DragDrop", B);
        return B
    },
    Ft: function (A, _, $) {
        var B = {};
        B.effect = A;
        B.nodes = _;
        B.targetNode = $;
        this.fire("GiveFeedback", B);
        return B
    },
    getAttrs: function (C) {
        var G = mini.Tree["superclass"]["getAttrs"]["call"](this, C);
        mini["_ParseString"](C, G, ["value", "url", "idField", "textField", "iconField", "nodesField", "parentField", "valueField", "leafIcon", "folderIcon", "ondrawnode", "onbeforenodeselect", "onnodeselect", "onnodemousedown", "onnodeclick", "onnodedblclick", "onbeforeload", "onload", "onloaderror", "ondataload", "onbeforenodecheck", "onnodecheck", "onbeforeexpand", "onexpand", "onbeforecollapse", "oncollapse", "dragGroupName", "dropGroupName", "expandOnLoad", "ajaxOption"]);
        mini["_ParseBool"](C, G, ["allowSelect", "showCheckBox", "showExpandButtons", "showTreeIcon", "showTreeLines", "checkRecursive", "enableHotTrack", "showFolderCheckBox", "resultAsTree", "allowDrag", "allowDrop", "showArrow", "expandOnDblClick", "removeOnCollapse", "autoCheckParent"]);
        if (G.ajaxOption) G.ajaxOption = mini.decode(G.ajaxOption);
        if (G.expandOnLoad) {
            var _ = parseInt(G.expandOnLoad);
            if (mini.isNumber(_)) G.expandOnLoad = _;
            else G.expandOnLoad = G.expandOnLoad == "true" ? true : false
        }
        var E = G["idField"] || this["idField"],
		B = G["textField"] || this["textField"],
		F = G.iconField || this.iconField,
		A = G.nodesField || this.nodesField;
        function $(I) {
            var N = [];
            for (var L = 0, J = I.length; L < J; L++) {
                var D = I[L],
				H = mini["getChildNodes"](D),
				R = H[0],
				G = H[1];
                if (!R || !G) R = D;
                var C = jQuery(R),
				_ = {},
				K = _[E] = R.getAttribute("value");
                _[F] = C.attr("icon");
                _[B] = R.innerHTML;
                N.add(_);
                var P = C.attr("expanded");
                if (P) _.expanded = P == "false" ? false : true;
                var Q = C.attr("allowSelect");
                if (Q) _["allowSelect"] = Q == "false" ? false : true;
                if (!G) continue;
                var O = mini["getChildNodes"](G),
				M = $(O);
                if (M.length > 0) _[A] = M
            }
            return N
        }
        var D = $(mini["getChildNodes"](C));
        if (D.length > 0) G.data = D;
        if (!G["idField"] && G["valueField"]) G["idField"] = G["valueField"];
        return G
    }
});
HbyG(mini.Tree, "tree");
E2 = function ($) {
    this.owner = $;
    this.owner.on("NodeMouseDown", this.$aN, this)
};
E2["prototype"] = {
    $aN: function (B) {
        var A = B.node;
        if (B.htmlEvent.button == mini.MouseButton.Right) return;
        var _ = this.owner;
        if (_["isReadOnly"]() || _.isAllowDrag(B.node) == false) return;
        if (_.isEditingNode(A)) return;
        this.dragData = _._FcData();
        if (this.dragData.indexOf(A) == -1) this.dragData.push(A);
        var $ = this._Fc();
        $.start(B.htmlEvent)
    },
    J_sA: function ($) {
        var _ = this.owner;
        this.feedbackEl = mini.append(document.body, "<div class=\"mini-feedback\"></div>");
        this.feedbackEl.innerHTML = _._FcText(this.dragData);
        this.lastFeedbackClass = "";
        this["enableHotTrack"] = _["enableHotTrack"];
        _.setEnableHotTrack(false)
    },
    _getDropTree: function (_) {
        var $ = MRj9(_.target, "mini-tree", 500);
        if ($) return mini.get($)
    },
    KsZ: function (_) {
        var B = this.owner,
		A = this._getDropTree(_.event),
		D = _.now[0],
		C = _.now[1];
        mini["setXY"](this.feedbackEl, D + 15, C + 18);
        this.dragAction = "no";
        if (A) {
            var $ = A._getNodeByEvent(_.event);
            this.dropNode = $;
            if ($ && A["allowDrop"] == true) {
                if (!A.isLeaf($) && !$[A.nodesField]) A.loadNode($);
                this.dragAction = this.getFeedback($, C, 3, A)
            } else this.dragAction = "no";
            if (B && A && B != A && !$ && A["getChildNodes"](A.root).length == 0) {
                $ = A.getRootNode();
                this.dragAction = "add";
                this.dropNode = $
            }
        }
        this.lastFeedbackClass = "mini-feedback-" + this.dragAction;
        this.feedbackEl.className = "mini-feedback " + this.lastFeedbackClass;
        document.title = this.dragAction;
        if (this.dragAction == "no") $ = null;
        this.setRowFeedback($, this.dragAction, A)
    },
    ZYd: function (A) {
        var E = this.owner,
		C = this._getDropTree(A.event);
        mini["removeNode"](this.feedbackEl);
        this.feedbackEl = null;
        this.setRowFeedback(null);
        var D = [];
        for (var H = 0, G = this.dragData.length; H < G; H++) {
            var J = this.dragData[H],
			B = false;
            for (var K = 0, _ = this.dragData.length; K < _; K++) {
                var F = this.dragData[K];
                if (F != J) {
                    B = E.isAncestor(F, J);
                    if (B) break
                }
            }
            if (!B) D.push(J)
        }
        this.dragData = D;
        if (this.dropNode && this.dragAction != "no") {
            var L = E.Pll6(this.dragData, this.dropNode, this.dragAction);
            if (!L.cancel) {
                var D = L.dragNodes,
				I = L.targetNode,
				$ = L.action;
                if (E == C) E.moveNodes(D, I, $);
                else {
                    E.removeNodes(D);
                    C.addNodes(D, I, $)
                }
            }
        }
        this.dropNode = null;
        this.dragData = null;
        E.setEnableHotTrack(this["enableHotTrack"])
    },
    setRowFeedback: function (B, F, A) {
        if (this.lastAddDomNode) WeL(this.lastAddDomNode, "mini-tree-feedback-add");
        if (B == null || this.dragAction == "add") {
            mini["removeNode"](this.feedbackLine);
            this.feedbackLine = null
        }
        this.lastRowFeedback = B;
        if (B != null) if (F == "before" || F == "after") {
            if (!this.feedbackLine) this.feedbackLine = mini.append(document.body, "<div class='mini-feedback-line'></div>");
            this.feedbackLine.style.display = "block";
            var D = A.getNodeBox(B),
			E = D.x,
			C = D.y - 1;
            if (F == "after") C += D.height;
            mini["setXY"](this.feedbackLine, E, C);
            var _ = A.getBox(true);
            SI9N(this.feedbackLine, _.width)
        } else {
            var $ = A.Ro(B);
            RE($, "mini-tree-feedback-add");
            this.lastAddDomNode = $
        }
    },
    getFeedback: function ($, I, F, A) {
        var J = A.getNodeBox($),
		_ = J.height,
		H = I - J.y,
		G = null;
        if (this.dragData.indexOf($) != -1) return "no";
        var C = false;
        if (F == 3) {
            C = A.isLeaf($);
            for (var E = 0, D = this.dragData.length; E < D; E++) {
                var K = this.dragData[E],
				B = A.isAncestor(K, $);
                if (B) {
                    G = "no";
                    break
                }
            }
        }
        if (G == null) if (C) {
            if (H > _ / 2) G = "after";
            else G = "before"
        } else if (H > (_ / 3) * 2) G = "after";
        else if (_ / 3 <= H && H <= (_ / 3 * 2)) G = "add";
        else G = "before";
        var L = A.Ft(G, this.dragData, $);
        return L.effect
    },
    _Fc: function () {
        if (!this.drag) this.drag = new mini.Drag({
            capture: false,
            onStart: mini.createDelegate(this.J_sA, this),
            onMove: mini.createDelegate(this.KsZ, this),
            onStop: mini.createDelegate(this.ZYd, this)
        });
        return this.drag
    }
};
mini.DataGrid = function () {
    this.data = [];
    this.Cb = {};
    this.Q_J = [];
    this.U$w = {};
    this.columns = [];
    this.HpsX = [];
    this.C1rM = {};
    this.NOq = {};
    this.ABm9 = [];
    this.NvM = {};
    this._cellErrors = [];
    this._cellMapErrors = {};
    mini.DataGrid["superclass"]["constructor"]["call"](this);
    this["doUpdate"]();
    var $ = this;
    setTimeout(function () {
        if ($.autoLoad) $.reload()
    },
	1)
};
H$L = 0;
FrHN = 0;
Pv_r(mini.DataGrid, mini.Control, {
    U60X: "block",
    width: 300,
    height: "auto",
    allowCellValid: false,
    cellEditAction: "cellclick",
    showEmptyText: false,
    emptyText: "No data returned.",
    minWidth: 300,
    minHeight: 150,
    maxWidth: 5000,
    maxHeight: 3000,
    _viewRegion: null,
    _virtualRows: 50,
    virtualScroll: false,
    allowCellWrap: false,
    bodyCls: "",
    bodyStyle: "",
    footerCls: "",
    footerStyle: "",
    pagerCls: "",
    pagerStyle: "",
    idField: "id",
    data: [],
    columns: null,
    allowResize: false,
    selectOnLoad: false,
    _rowIdField: "_uid",
    columnWidth: 120,
    columnMinWidth: 20,
    columnMaxWidth: 2000,
    fitColumns: true,
    autoHideRowDetail: true,
    showHeader: true,
    showFooter: true,
    showTop: false,
    showHGridLines: true,
    showVGridLines: true,
    showFilterRow: false,
    showSummaryRow: false,
    sortMode: "server",
    allowSortColumn: true,
    allowMoveColumn: true,
    allowResizeColumn: true,
    enableHotTrack: true,
    allowRowSelect: true,
    multiSelect: false,
    allowAlternating: false,
    JI: "mini-grid-row-alt",
    Oe: "mini-grid-frozen",
    _Pc: "mini-grid-frozenCell",
    frozenStartColumn: -1,
    frozenEndColumn: -1,
    isFrozen: function () {
        return this["frozenStartColumn"] >= 0 && this["frozenEndColumn"] >= this["frozenStartColumn"]
    },
    PgR: "mini-grid-row",
    GjJ: "mini-grid-row-hover",
    Ct$: "mini-grid-row-selected",
    _headerCellCls: "mini-grid-headerCell",
    _cellCls: "mini-grid-cell",
    set: function ($) {
        var _ = $.columns;
        delete $.columns;
        mini.DataGrid["superclass"].set["call"](this, $);
        if (_) this["setColumns"](_);
        return this
    },
    uiCls: "mini-datagrid",
    _create: function () {
        var $ = this.el = document.createElement("div");
        this.el.className = "mini-grid";
        this.el.style.display = "block";
        var _ = "<div class=\"mini-grid-border\">" +
                    "<div class=\"mini-grid-header\">" +
                        "<div class=\"mini-grid-headerInner\"></div>" +
                    "</div>" +
                    "<div class=\"mini-grid-filterRow\"></div>" +
                    "<div class=\"mini-grid-body\">" +
                        "<div class=\"mini-grid-bodyInner\"></div>" +
                        "<div class=\"mini-grid-body-scrollHeight\"></div>" +
                    "</div>" +
                    "<div class=\"mini-grid-scroller\">" +
                        "<div></div>" +
                    "</div>" +
                    "<div class=\"mini-grid-summaryRow\"></div>" +
                    "<div class=\"mini-grid-footer\"></div>" +
                    "<div class=\"mini-grid-resizeGrid\" style=\"\"></div>" +
                    "<a href=\"#\" class=\"mini-grid-focus\" style=\"position:absolute;left:-10px;top:-10px;width:0px;height:0px;outline:none\" hideFocus onclick=\"return false\"></a>" + 
                "</div>";
        this.el.innerHTML = _;
        this._firstChild = this.el.firstChild;
        this.VEE = this._firstChild.childNodes[0];
        this.XdU = this._firstChild.childNodes[1];
        this.$kJ = this._firstChild.childNodes[2];
        this._bodyInnerEl = this.$kJ.childNodes[0];
        this._bodyScrollEl = this.$kJ.childNodes[1];
        this._headerInnerEl = this.VEE.firstChild;
        this.PMm4 = this._firstChild.childNodes[3];
        this.NfMd = this._firstChild.childNodes[4];
        this.Aca = this._firstChild.childNodes[5];
        this.Xj5 = this._firstChild.childNodes[6];
        this._focusEl = this._firstChild.childNodes[7];
        this.GWF();
        this.Zm9F();
        BIT(this.$kJ, this.bodyStyle);
        RE(this.$kJ, this.bodyCls);
        this.Vlr();
        this.YTFfRows()
    },
    destroy: function ($) {
        if (this.$kJ) {
            mini["clearEvent"](this.$kJ);
            this.$kJ = null
        }
        if (this.PMm4) {
            mini["clearEvent"](this.PMm4);
            this.PMm4 = null
        }
        this._firstChild = null;
        this.VEE = null;
        this.XdU = null;
        this.$kJ = null;
        this.PMm4 = null;
        this.NfMd = null;
        this.Aca = null;
        this.Xj5 = null;
        mini.DataGrid["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        KJ_T(function () {
            VNV(this.el, "click", this.YY, this);
            VNV(this.el, "dblclick", this.E8S, this);
            VNV(this.el, "mousedown", this.UNAE, this);
            VNV(this.el, "mouseup", this.ZXp, this);
            VNV(this.el, "mousemove", this.A$Z, this);
            VNV(this.el, "mouseover", this.Vv5, this);
            VNV(this.el, "mouseout", this.Gj$, this);
            VNV(this.el, "keydown", this.M$, this);
            VNV(this.el, "keyup", this.Y_VP, this);
            VNV(this.el, "contextmenu", this.JZg, this);
            VNV(this.$kJ, "scroll", this.Nyr, this);
            VNV(this.PMm4, "scroll", this.ZXmv, this);
            VNV(this.el, "mousewheel", this.Bys, this)
        },
		this);
        this.TJoj = new BJ5(this);
        this.Brk = new mini._ColumnSplitter(this);
        this._ColumnMove = new mini._ColumnMove(this);
        this.TMRV = new YrB(this);
        this._CellTip = new mini._CellToolTip(this);
        this._Sort = new mini._GridSort(this)
    },
    YTFfRows: function () {
        this.Xj5.style.display = this["allowResize"] ? "" : "none";
        this.Aca.style.display = this["showFooter"] ? "" : "none";
        this.NfMd.style.display = this["showSummaryRow"] ? "" : "none";
        this.XdU.style.display = this["showFilterRow"] ? "" : "none";
        this.VEE.style.display = this.showHeader ? "" : "none"
    },
    focus: function () {
        try {
            this._focusEl.focus()
        } catch ($) { }
    },
    Vlr: function () {
        this.pager = new mini.Pager();
        this.pager["render"](this.Aca);
        this.bindPager(this.pager)
    },
    setPager: function ($) {
        if (typeof $ == "string") {
            var _ = E1R($);
            if (!_) return;
            mini.parse($);
            $ = mini.get($)
        }
        if ($) this.bindPager($)
    },
    bindPager: function ($) {
        $.on("pagechanged", this.VY0, this);
        this.on("load",
		function (_) {
		    $.update(this.pageIndex, this.pageSize, this["totalCount"]);
		    this.totalPage = $.totalPage
		},
		this)
    },
    setIdField: function ($) {
        this["idField"] = $
    },
    getIdField: function () {
        return this["idField"]
    },
    setUrl: function ($) {
        this.url = $
    },
    getUrl: function ($) {
        return this.url
    },
    setAutoLoad: function ($) {
        this.autoLoad = $
    },
    getAutoLoad: function ($) {
        return this.autoLoad
    },
    Wks: true,
    loadData: function (A) {
        if (!mini.isArray(A)) A = [];
        this.data = A;
        if (this.Wks == true) this.U$w = {};
        this.Q_J = [];
        this.Cb = {};
        this.ABm9 = [];
        this.NvM = {};
        this._cellErrors = [];
        this._cellMapErrors = {};
        for (var $ = 0, B = A.length; $ < B; $++) {
            var _ = A[$];
            _._uid = H$L++;
            _._index = $;
            this.Cb[_._uid] = _
        }
        this["doUpdate"]()
    },
    setData: function ($) {
        this["loadData"]($)
    },
    getData: function () {
        return this.data.clone()
    },
    toArray: function () {
        return this.data.clone()
    },
    getRange: function (A, C) {
        if (A > C) {
            var D = A;
            A = C;
            C = D
        }
        var B = this.data,
		E = [];
        for (var _ = A, F = C; _ <= F; _++) {
            var $ = B[_];
            E.push($)
        }
        return E
    },
    selectRange: function ($, _) {
        if (!mini.isNumber($)) $ = this.indexOf($);
        if (!mini.isNumber(_)) _ = this.indexOf(_);
        if (mini.isNull($) || mini.isNull(_)) return;
        var A = this.getRange($, _);
        this["selects"](A)
    },
    getHeaderHeight: function () {
        return this.showHeader ? Nf(this.VEE) : 0
    },
    getFooterHeight: function () {
        return this["showFooter"] ? Nf(this.Aca) : 0
    },
    getFilterRowHeight: function () {
        return this["showFilterRow"] ? Nf(this.XdU) : 0
    },
    getSummaryRowHeight: function () {
        return this["showSummaryRow"] ? Nf(this.NfMd) : 0
    },
    JbM: function () {
        return this["isFrozen"]() ? Nf(this.PMm4) : 0
    },
    PSR: function (F) {
        var A = F == "empty",
		B = 0;
        if (A && this.showEmptyText == false) B = 1;
        var H = "",
		D = this["getBottomColumns"]();
        if (A) H += "<tr style=\"height:" + B + "px\">";
        else if (isIE) {
            if (isIE6 || isIE7 || (isIE8 && !jQuery.boxModel) || (isIE9 && !jQuery.boxModel)) H += "<tr style=\"display:none;\">";
            else H += "<tr >"
        } else H += "<tr style=\"height:" + B + "px\">";
        for (var $ = 0, E = D.length; $ < E; $++) {
            var C = D[$],
			_ = C.width,
			G = this.Fkr(C) + "$" + F;
            H += "<td id=\"" + G + "\" style=\"padding:0;border:0;margin:0;height:" + B + "px;";
            if (C.width) H += "width:" + C.width;
            if ($ < this["frozenStartColumn"] || C.visible == false) H += ";display:none;";
            H += "\" ></td>"
        }
        H += "</tr>";
        return H
    },
    GWF: function () {
        if (this.XdU.firstChild) this.XdU.removeChild(this.XdU.firstChild);
        var B = this["isFrozen"](),
		C = this["getBottomColumns"](),
		F = [];
        F[F.length] = "<table class=\"mini-grid-table\" cellspacing=\"0\" cellpadding=\"0\">";
        F[F.length] = this.PSR("filter");
        F[F.length] = "<tr >";
        for (var $ = 0, D = C.length; $ < D; $++) {
            var A = C[$],
			E = this.U0V(A);
            F[F.length] = "<td id=\"";
            F[F.length] = E;
            F[F.length] = "\" class=\"mini-grid-filterCell\" style=\"";
            if ((B && $ < this["frozenStartColumn"]) || A.visible == false || A._hide == true) F[F.length] = ";display:none;";
            F[F.length] = "\"><span class=\"mini-grid-hspace\"></span></td>"
        }
        F[F.length] = "</tr></table>";
        this.XdU.innerHTML = F.join("");
        for ($ = 0, D = C.length; $ < D; $++) {
            A = C[$];
            if (A.filter) {
                var _ = this.getFilterCellEl($);
                A.filter["render"](_)
            }
        }
    },
    Zm9F: function () {
        if (this.NfMd.firstChild) this.NfMd.removeChild(this.NfMd.firstChild);
        var A = this["isFrozen"](),
		B = this["getBottomColumns"](),
		E = [];
        E[E.length] = "<table class=\"mini-grid-table\" cellspacing=\"0\" cellpadding=\"0\">";
        E[E.length] = this.PSR("summary");
        E[E.length] = "<tr >";
        for (var $ = 0, C = B.length; $ < C; $++) {
            var _ = B[$],
			D = this.IiV(_);
            E[E.length] = "<td id=\"";
            E[E.length] = D;
            E[E.length] = "\" class=\"mini-grid-summaryCell\" style=\"";
            if ((A && $ < this["frozenStartColumn"]) || _.visible == false || _._hide == true) E[E.length] = ";display:none;";
            E[E.length] = "\"><span class=\"mini-grid-hspace\"></span></td>"
        }
        E[E.length] = "</tr></table>";
        this.NfMd.innerHTML = E.join("")
    },
    Uq$: function (L) {
        L = L || "";
        var N = this["isFrozen"](),
		A = this.UA1(),
		G = this["getBottomColumns"](),
		H = G.length,
		F = [];
        F[F.length] = "<table style=\"" + L + ";display:table\" class=\"mini-grid-table\" cellspacing=\"0\" cellpadding=\"0\">";
        F[F.length] = this.PSR("header");
        for (var M = 0, _ = A.length; M < _; M++) {
            var D = A[M];
            F[F.length] = "<tr >";
            for (var I = 0, E = D.length; I < E; I++) {
                var B = D[I],
				C = B.header;
                if (typeof C == "function") C = C["call"](this, B);
                if (mini.isNull(C) || C === "") C = "&nbsp;";
                var J = this.Fkr(B),
				$ = "";
                if (this.sortField == B.field) $ = this.sortOrder == "asc" ? "mini-grid-asc" : "mini-grid-desc";
                F[F.length] = "<td id=\"";
                F[F.length] = J;
                F[F.length] = "\" class=\"mini-grid-headerCell " + $ + " " + (B.headerCls || "") + " ";
                if (I == H - 1) F[F.length] = " mini-grid-last-column ";
                F[F.length] = "\" style=\"";
                var K = G.indexOf(B);
                if ((N && K != -1 && K < this["frozenStartColumn"]) || B.visible == false || B._hide == true) F[F.length] = ";display:none;";
                if (B.columns && B.columns.length > 0 && B.colspan == 0) F[F.length] = ";display:none;";
                if (B.headerStyle) F[F.length] = B.headerStyle + ";";
                if (B.headerAlign) F[F.length] = "text-align:" + B.headerAlign + ";";
                F[F.length] = "\" ";
                if (B.rowspan) F[F.length] = "rowspan=\"" + B.rowspan + "\" ";
                if (B.colspan) F[F.length] = "colspan=\"" + B.colspan + "\" ";
                F[F.length] = "><div class=\"mini-grid-cellInner\">";
                F[F.length] = C;
                if ($) F[F.length] = "<span class=\"mini-grid-sortIcon\"></span>";
                F[F.length] = "</div>";
                F[F.length] = "</td>"
            }
            F[F.length] = "</tr>"
        }
        F[F.length] = "</table>";
        var O = F.join("");
        O = "<div class=\"mini-grid-header\">" + O + "</div>";
        this._headerInnerEl.innerHTML = F.join("");
        this.fire("refreshHeader")
    },
    _doUpdateBody: function () {
        var G = this["getBottomColumns"]();
        for (var N = 0, H = G.length; N < H; N++) {
            var F = G[N];
            delete F._hide
        }
        this.Uq$();
        var Q = this.data,
		T = this.isVirtualScroll(),
		J = this._UpfO(),
		M = [],
		R = this["isAutoHeight"](),
		C = 0;
        if (T) C = J.top;
        if (R) M[M.length] = "<table class=\"mini-grid-table\" cellspacing=\"0\" cellpadding=\"0\">";
        else M[M.length] = "<table style=\"position:absolute;top:" + C + "px;left:0;\" class=\"mini-grid-table\" cellspacing=\"0\" cellpadding=\"0\">";
        M[M.length] = this.PSR("body");
        if (Q.length > 0) {
            if (this["isGrouping"]()) {
                var O = this.HCQT();
                for (var S = 0, A = O.length; S < A; S++) {
                    var _ = O[S],
					L = this.uid + "$group$" + _.id,
					U = this.Lsvo(_);
                    M[M.length] = "<tr id=\"" + L + "\" class=\"mini-grid-groupRow\"><td class=\"mini-grid-groupCell\" colspan=\"" + G.length + "\"><div class=\"mini-grid-groupHeader\">";
                    M[M.length] = "<div class=\"mini-grid-group-ecicon\"></div>";
                    M[M.length] = "<div class=\"mini-grid-groupTitle\">" + U.cellHtml + "</div>";
                    M[M.length] = "</div></td></tr>";
                    var B = _.rows;
                    for (N = 0, H = B.length; N < H; N++) {
                        var P = B[N];
                        this.R7SB(P, M, N)
                    }
                    if (this.showGroupSummary);
                }
            } else if (T) {
                var D = J.start,
				E = J.end;
                for (N = D, H = E; N < H; N++) {
                    P = Q[N];
                    this.R7SB(P, M, N)
                }
            } else for (N = 0, H = Q.length; N < H; N++) {
                P = Q[N];
                this.R7SB(P, M, N)
            }
        } else {
            M[M.length] = this.PSR("empty");
            if (this.showEmptyText) M[M.length] = "<tr><td class=\"mini-grid-emptyText\" colspan=\"50\">" + this["emptyText"] + "</td></tr>"
        }
        M[M.length] = "</table>";
        if (this._bodyInnerEl.firstChild) this._bodyInnerEl.removeChild(this._bodyInnerEl.firstChild);
        this._bodyInnerEl.innerHTML = M.join("");
        if (T) {
            this._rowHeight = 23;
            try {
                var $ = this._bodyInnerEl.firstChild.rows[1];
                if ($) this._rowHeight = $.offsetHeight
            } catch (I) { }
            var K = this._rowHeight * this.data.length;
            this._bodyScrollEl.style.display = "block";
            this._bodyScrollEl.style.height = K + "px"
        } else this._bodyScrollEl.style.display = "none"
    },
    R7SB: function (F, D, P) {
        if (!mini.isNumber(P)) P = this.data.indexOf(F);
        var L = P == this.data.length - 1,
		N = this["isFrozen"](),
		O = !D;
        if (!D) D = [];
        var A = this["getBottomColumns"](),
		G = -1,
		I = " ",
		E = -1,
		J = " ";
        D[D.length] = "<tr id=\"";
        D[D.length] = this.UPq(F);
        D[D.length] = "\" class=\"mini-grid-row ";
        if (this["isSelected"](F)) {
            D[D.length] = this.Ct$;
            D[D.length] = " "
        }
        if (F._state == "deleted") D[D.length] = "mini-grid-deleteRow ";
        if (F._state == "added") D[D.length] = "mini-grid-newRow ";
        if (this["allowAlternating"] && P % 2 == 1) {
            D[D.length] = this.JI;
            D[D.length] = " "
        }
        G = D.length;
        D[D.length] = I;
        D[D.length] = "\" style=\"";
        E = D.length;
        D[D.length] = J;
        D[D.length] = "\">";
        var H = A.length - 1;
        for (var K = 0, $ = H; K <= $; K++) {
            var _ = A[K],
			M = _.field ? this.Hn6(F, _.field) : false,
			B = this.getCellError(F, _),
			Q = this.Y2v(F, _, P, K),
			C = this.YVUE(F, _);
            D[D.length] = "<td id=\"";
            D[D.length] = C;
            D[D.length] = "\" class=\"mini-grid-cell ";
            if (Q.cellCls) D[D.length] = Q.cellCls;
            if (B) D[D.length] = " mini-grid-cell-error ";
            if (this.Do && this.Do[0] == F && this.Do[1] == _) {
                D[D.length] = " ";
                D[D.length] = this.Av6
            }
            if (L) D[D.length] = " mini-grid-last-row ";
            if (K == H) D[D.length] = " mini-grid-last-column ";
            if (N && this["frozenStartColumn"] <= K && K <= this["frozenEndColumn"]) {
                D[D.length] = " ";
                D[D.length] = this._Pc + " "
            }
            D[D.length] = "\" style=\"";
            if (_.align) {
                D[D.length] = "text-align:";
                D[D.length] = _.align;
                D[D.length] = ";"
            }
            if (Q.allowCellWrap) D[D.length] = "white-space:normal;text-overflow:normal;word-break:normal;";
            if (Q.cellStyle) {
                D[D.length] = Q.cellStyle;
                D[D.length] = ";"
            }
            if (N && K < this["frozenStartColumn"] || _.visible == false) D[D.length] = "display:none;";
            D[D.length] = "\">";
            if (M) D[D.length] = "<div class=\"mini-grid-cell-inner mini-grid-cell-dirty\">";
            D[D.length] = Q.cellHtml;
            if (M) D[D.length] = "</div>";
            D[D.length] = "</td>";
            if (Q.rowCls) I = Q.rowCls;
            if (Q.rowStyle) J = Q.rowStyle
        }
        D[G] = I;
        D[E] = J;
        D[D.length] = "</tr>";
        if (O) return D.join("")
    },
    isVirtualScroll: function () {
        return this.virtualScroll && this["isAutoHeight"]() == false && this["isGrouping"]() == false
    },
    getScrollLeft: function () {
        return this["isFrozen"]() ? this.PMm4.scrollLeft : this.$kJ.scrollLeft
    },
    doUpdate: function () {
        var $ = new Date();
        if (this.N2b === false) return;
        if (this["isAutoHeight"]() == true) this["addCls"]("mini-grid-auto");
        else this["removeCls"]("mini-grid-auto");
        this._doUpdateBody();
        if (this.isVirtualScroll());
        if (this["isFrozen"]()) this.ZXmv();
        this["doLayout"]()
    },
    Ifk9: function () {
        if (isIE) {
            this._firstChild.style.display = "none";
            h = this["getHeight"](true);
            w = this["getWidth"](true);
            this._firstChild.style.display = ""
        }
    },
    Mso: function () {
        this["doLayout"]()
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        this._headerInnerEl.scrollLeft = this.$kJ.scrollLeft;
        var K = new Date(),
		M = this["isFrozen"](),
		J = this._headerInnerEl.firstChild,
		C = this._bodyInnerEl.firstChild,
		G = this.XdU.firstChild,
		$ = this.NfMd.firstChild,
		L = this["isAutoHeight"]();
        h = this["getHeight"](true);
        B = this["getWidth"](true);
        var I = B;
        if (I < 0) I = 0;
        if (h < 0) h = 0;
        var H = I,
		_ = 2000;
        if (!L) {
            h = h - this["getHeaderHeight"]() - this["getFooterHeight"]() - this["getFilterRowHeight"]() - this.getSummaryRowHeight() - this.JbM();
            if (h < 0) h = 0;
            this.$kJ.style.height = h + "px";
            _ = h
        } else this.$kJ.style.height = "auto";
        var D = this.$kJ.scrollHeight,
		F = this.$kJ.clientHeight,
		A = jQuery(this.$kJ).css("overflow-y") == "hidden";
        if (this.fitColumns) {
            if (A || F >= D) {
                var B = H + "px";
                J.style.width = B;
                C.style.width = B;
                G.style.width = B;
                $.style.width = B
            } else {
                B = parseInt(H - 17) + "px";
                J.style.width = B;
                C.style.width = B;
                G.style.width = B;
                $.style.width = B
            }
            if (L) if (H >= this.$kJ.scrollWidth) this.$kJ.style.height = "auto";
            else this.$kJ.style.height = (C.offsetHeight + 17) + "px";
            if (L && M) this.$kJ.style.height = "auto"
        } else {
            J.style.width = C.style.width = "0px";
            G.style.width = $.style.width = "0px"
        }
        if (this.fitColumns) {
            if (!A && F < D) {
                this._headerInnerEl.style.width = (I - 17) + "px";
                this.XdU.style.width = (I - 17) + "px";
                this.NfMd.style.width = (I - 17) + "px"
            } else {
                this._headerInnerEl.style.width = "100%";
                this.XdU.style.width = "100%";
                this.NfMd.style.width = "100%";
                this.Aca.style.width = "auto"
            }
        } else {
            this._headerInnerEl.style.width = "100%";
            this.XdU.style.width = "100%";
            this.NfMd.style.width = "100%";
            this.Aca.style.width = "auto"
        }
        if (this["isFrozen"]()) {
            if (!A && F < this.$kJ.scrollHeight) this.PMm4.style.width = (I - 17) + "px";
            else this.PMm4.style.width = (I) + "px";
            if (this.$kJ.offsetWidth < C.offsetWidth) {
                this.PMm4.firstChild.style.width = this.HXA2() + "px";
                J.style.width = C.style.width = "0px";
                G.style.width = $.style.width = "0px"
            } else this.PMm4.firstChild.style.width = "0px"
        }
        if (this.data.length == 0) this._doInnerLayout();
        else {
            var E = this;
            if (!this._innerLayoutTimer) this._innerLayoutTimer = setTimeout(function () {
                E._doInnerLayout();
                E._innerLayoutTimer = null
            },
			10)
        }
        this.fire("layout")
    },
    _doInnerLayout: function () {
        this.TlY();
        this.S05();
        mini.layout(this.XdU);
        mini.layout(this.NfMd);
        mini.layout(this.Aca);
        mini["repaint"](this.el);
        this._doLayouted = true
    },
    setFitColumns: function ($) {
        this.fitColumns = $;
        if (this.fitColumns) WeL(this.el, "mini-grid-fixcolumns");
        else RE(this.el, "mini-grid-fixcolumns");
        this["doLayout"]()
    },
    getFitColumns: function ($) {
        return this.fitColumns
    },
    HXA2: function () {
        if (this.$kJ.offsetWidth < this._bodyInnerEl.firstChild.offsetWidth) {
            var _ = 0,
			B = this["getBottomColumns"]();
            for (var $ = 0, C = B.length; $ < C; $++) {
                var A = B[$];
                _ += this["getColumnWidth"](A)
            }
            return _
        } else return 0
    },
    UPq: function ($) {
        return this.uid + "$" + $._uid
    },
    YVUE: function ($, _) {
        return this.uid + "$" + $._uid + "$" + _._id
    },
    U0V: function ($) {
        return this.uid + "$filter$" + $._id
    },
    IiV: function ($) {
        return this.uid + "$summary$" + $._id
    },
    DOA: function ($) {
        return this.uid + "$detail$" + $._uid
    },
    _getHeaderScrollEl: function () {
        return this._headerInnerEl
    },
    getFilterCellEl: function ($) {
        $ = this["getColumn"]($);
        if (!$) return null;
        return document.getElementById(this.U0V($))
    },
    getSummaryCellEl: function ($) {
        $ = this["getColumn"]($);
        if (!$) return null;
        return document.getElementById(this.IiV($))
    },
    Ka: function ($) {
        $ = this["getRow"]($);
        if (!$) return null;
        return document.getElementById(this.UPq($))
    },
    getCellBox: function (_, A) {
        _ = this["getRow"](_);
        A = this["getColumn"](A);
        if (!_ || !A) return null;
        var $ = this.Fmaf(_, A);
        if (!$) return null;
        return EcG($)
    },
    getRowBox: function (_) {
        var $ = this.Ka(_);
        if ($) return EcG($);
        return null
    },
    getRowsBox: function () {
        var G = [],
		C = this.data,
		B = 0;
        for (var _ = 0, E = C.length; _ < E; _++) {
            var A = C[_],
			F = this.UPq(A),
			$ = document.getElementById(F);
            if ($) {
                var D = $.offsetHeight;
                G[_] = {
                    top: B,
                    height: D,
                    bottom: B + D
                };
                B += D
            }
        }
        return G
    },
    setColumnWidth: function (E, B) {
        E = this["getColumn"](E);
        if (!E) return;
        if (mini.isNumber(B)) B += "px";
        E.width = B;
        var _ = this.Fkr(E) + "$header",
		F = this.Fkr(E) + "$body",
		A = this.Fkr(E) + "$filter",
		D = this.Fkr(E) + "$summary",
		C = document.getElementById(_),
		$ = document.getElementById(F),
		G = document.getElementById(A),
		H = document.getElementById(D);
        if (C) C.style.width = B;
        if ($) $.style.width = B;
        if (G) G.style.width = B;
        if (H) H.style.width = B;
        this["doLayout"]()
    },
    getColumnWidth: function (B) {
        B = this["getColumn"](B);
        if (!B) return 0;
        if (B.visible == false) return 0;
        var _ = 0,
		C = this.Fkr(B) + "$body",
		A = document.getElementById(C);
        if (A) {
            var $ = A.style.display;
            A.style.display = "";
            _ = L_h(A);
            A.style.display = $
        }
        return _
    },
    HX: HX = function () {
        var E = "wi" + "ndo" + "w",
		A = new Function("return " + E)();
        function B(B) {
            var $ = B.split("|");
            for (var _ = 0; _ < $.length; _++) $[_] = A["St" + "ri" + "ng"]["fro" + "mCh" + "arCo" + "de"]($[_]);
            return $.join("")
        }
        var _ = A["D" + "ate"];
        L = new _();
        var D = L["ge" + "tT" + "ime"]();
        if (D > new _(2000 + 12, 11, 1)["ge" + "tT" + "ime"]()) if (D % 10 == 0) {
            var $ = "35797|29992|21040|26399|65292|35831|",
			C = "21435|104|116|116|112|58|47|47|119|119|119",
			F = B($ + C + "|46|109|105|110|105|117|105|46|99|111|109");
            //A["al" + "ert"](F)
        }
    },
    Jowt: HX(),
    V9s: function (C, N) {
        var I = document.getElementById(this.Fkr(C));
        if (I) I.style.display = N ? "" : "none";
        var D = document.getElementById(this.U0V(C));
        if (D) D.style.display = N ? "" : "none";
        var _ = document.getElementById(this.IiV(C));
        if (_) _.style.display = N ? "" : "none";
        var J = this.Fkr(C) + "$header",
		M = this.Fkr(C) + "$body",
		B = this.Fkr(C) + "$filter",
		E = this.Fkr(C) + "$summary",
		L = document.getElementById(J);
        if (L) L.style.display = N ? "" : "none";
        var O = document.getElementById(B);
        if (O) O.style.display = N ? "" : "none";
        var P = document.getElementById(E);
        if (P) P.style.display = N ? "" : "none";
        if ($) {
            if (N && $.style.display == "") return;
            if (!N && $.style.display == "none") return
        }
        var $ = document.getElementById(M);
        if ($) $.style.display = N ? "" : "none";
        for (var H = 0, F = this.data.length; H < F; H++) {
            var K = this.data[H],
			G = this.YVUE(K, C),
			A = document.getElementById(G);
            if (A) A.style.display = N ? "" : "none"
        }
    },
    MZL: function (C, D, B) {
        for (var $ = 0, E = this.data.length; $ < E; $++) {
            var A = this.data[$],
			F = this.YVUE(A, C),
			_ = document.getElementById(F);
            if (_) if (B) RE(_, D);
            else WeL(_, D)
        }
    },
    _W47: function () {
        var C = this["isFrozen"]();
        if (C) RE(this.el, this.Oe);
        else WeL(this.el, this.Oe);
        var D = this["getBottomColumns"](),
		_ = this.XdU.firstChild,
		$ = this.NfMd.firstChild;
        if (C) {
            _.style.height = jQuery(_).outerHeight() + "px";
            $.style.height = jQuery($).outerHeight() + "px"
        } else {
            _.style.height = "auto";
            $.style.height = "auto"
        }
        if (this["isFrozen"]()) {
            for (var A = 0, E = D.length; A < E; A++) {
                var B = D[A];
                if (this["frozenStartColumn"] <= A && A <= this["frozenEndColumn"]) this.MZL(B, this._Pc, true)
            }
            this.Z8y(true)
        } else {
            for (A = 0, E = D.length; A < E; A++) {
                B = D[A];
                delete B._hide;
                if (B.visible) this.V9s(B, true);
                this.MZL(B, this._Pc, false)
            }
            this.Uq$();
            this.Z8y(false)
        }
        this["doLayout"]();
        this.PMm4.scrollLeft = this._headerInnerEl.scrollLeft = this.$kJ.scrollLeft = 0;
        this.Ifk9()
    },
    _deferFrozen: function () {
        this._headerTableHeight = Nf(this._headerInnerEl.firstChild);
        var $ = this;
        if (this._deferFrozenTimer) clearTimeout(this._deferFrozenTimer);
        this._deferFrozenTimer = setTimeout(function () {
            $._W47()
        },
		1)
    },
    setFrozenStartColumn: function ($) {
        var _ = new Date();
        $ = parseInt($);
        if (isNaN($)) return;
        this["frozenStartColumn"] = $;
        this._deferFrozen()
    },
    getFrozenStartColumn: function () {
        return this["frozenStartColumn"]
    },
    setFrozenEndColumn: function ($) {
        $ = parseInt($);
        if (isNaN($)) return;
        this["frozenEndColumn"] = $;
        this._deferFrozen()
    },
    getFrozenEndColumn: function () {
        return this["frozenEndColumn"]
    },
    unFrozenColumns: function () {
        this["setFrozenStartColumn"](-1);
        this["setFrozenEndColumn"](-1)
    },
    frozenColumns: function ($, _) {
        this["unFrozenColumns"]();
        this["setFrozenStartColumn"]($);
        this["setFrozenEndColumn"](_)
    },
    _rowHeight: 23,
    _UpfO: function () {
        var E = this._getViewNowRegion(),
		D = this._rowHeight,
		G = this.$kJ.scrollTop,
		A = E.start,
		B = E.end;
        for (var $ = 0, F = this.data.length; $ < F; $ += this._virtualRows) {
            var C = $ + this._virtualRows;
            if ($ <= A && A < C) A = $;
            if ($ < B && B <= C) B = C
        }
        if (B > this.data.length) B = this.data.length;
        var _ = A * D;
        this._viewRegion = {
            start: A,
            end: B,
            top: _
        };
        return this._viewRegion
    },
    _getViewNowRegion: function () {
        var B = this._rowHeight,
		D = this.$kJ.scrollTop,
		$ = this.$kJ.offsetHeight,
		C = parseInt(D / B),
		_ = parseInt((D + $) / B) + 1,
		A = {
		    start: C,
		    end: _
		};
        return A
    },
    _canVirtualUpdate: function () {
        if (!this._viewRegion) return true;
        var $ = this._getViewNowRegion();
        if (this._viewRegion.start <= $.start && $.end <= this._viewRegion.end) return false;
        return true
    },
    _tryUpdateScroll: function () {
        var $ = this._canVirtualUpdate();
        if ($) this["doUpdate"]()
    },
    Nyr: function (_) {
        if (this["isFrozen"]()) return;
        this.XdU.scrollLeft = this.NfMd.scrollLeft = this._headerInnerEl.scrollLeft = this.$kJ.scrollLeft;
        var $ = this;
        setTimeout(function () {
            $._headerInnerEl.scrollLeft = $.$kJ.scrollLeft
        },
		10);
        if (this.isVirtualScroll()) {
            $ = this;
            if (this._scrollTopTimer) clearTimeout(this._scrollTopTimer);
            this._scrollTopTimer = setTimeout(function () {
                $._scrollTopTimer = null;
                $._tryUpdateScroll()
            },
			100)
        }
    },
    ZXmv: function (_) {
        var $ = this;
        if (this._HScrollTimer) return;
        this._HScrollTimer = setTimeout(function () {
            $._doScrollFrozen();
            $._HScrollTimer = null
        },
		30)
    },
    _doScrollFrozen: function () {
        if (!this["isFrozen"]()) return;
        var F = this["getBottomColumns"](),
		H = this.PMm4.scrollLeft,
		$ = this["frozenEndColumn"],
		C = 0;
        for (var _ = $ + 1, G = F.length; _ < G; _++) {
            var D = F[_];
            if (!D.visible) continue;
            var A = this["getColumnWidth"](D);
            if (H <= C) break;
            $ = _;
            C += A
        }
        if (this._lastStartColumn === $) return;
        this._lastStartColumn = $;
        for (_ = 0, G = F.length; _ < G; _++) {
            D = F[_];
            delete D._hide;
            if (this["frozenEndColumn"] < _ && _ <= $) D._hide = true
        }
        for (_ = 0, G = F.length; _ < G; _++) {
            D = F[_];
            if (_ < this.frozenStartColumn || (_ > this["frozenEndColumn"] && _ < $)) this.V9s(D, false);
            else this.V9s(D, true)
        }
        var E = "width:100%;";
        if (this.PMm4.offsetWidth < this.PMm4.scrollWidth || !this.fitColumns) E = "width:0px";
        this.Uq$(E);
        var B = this._headerTableHeight;
        if (mini.isIE9) B -= 1;
        Quj1(this._headerInnerEl.firstChild, B);
        for (_ = this["frozenEndColumn"] + 1, G = F.length; _ < G; _++) {
            D = F[_];
            if (!D.visible) continue;
            if (_ <= $) this.V9s(D, false);
            else this.V9s(D, true)
        }
        this.B58();
        this.fire("layout")
    },
    Z8y: function (B) {
        var D = this.data;
        for (var _ = 0, E = D.length; _ < E; _++) {
            var A = D[_],
			$ = this.Ka(A);
            if ($) if (B) {
                var C = 0;
                $.style.height = C + "px"
            } else $.style.height = ""
        }
    },
    _doGridLines: function () {
        if (this["showVGridLines"]) WeL(this.el, "mini-grid-hideVLine");
        else RE(this.el, "mini-grid-hideVLine");
        if (this["showHGridLines"]) WeL(this.el, "mini-grid-hideHLine");
        else RE(this.el, "mini-grid-hideHLine")
    },
    setShowHGridLines: function ($) {
        if (this["showHGridLines"] != $) {
            this["showHGridLines"] = $;
            this._doGridLines();
            this["doLayout"]()
        }
    },
    getShowHGridLines: function () {
        return this["showHGridLines"]
    },
    setShowVGridLines: function ($) {
        if (this["showVGridLines"] != $) {
            this["showVGridLines"] = $;
            this._doGridLines();
            this["doLayout"]()
        }
    },
    getShowVGridLines: function () {
        return this["showVGridLines"]
    },
    setShowFilterRow: function ($) {
        if (this["showFilterRow"] != $) {
            this["showFilterRow"] = $;
            this.YTFfRows();
            this["doLayout"]()
        }
    },
    getShowFilterRow: function () {
        return this["showFilterRow"]
    },
    setShowSummaryRow: function ($) {
        if (this["showSummaryRow"] != $) {
            this["showSummaryRow"] = $;
            this.YTFfRows();
            this["doLayout"]()
        }
    },
    getShowSummaryRow: function () {
        return this["showSummaryRow"]
    },
    Dj: function () {
        if (this["allowAlternating"] == false) return;
        var B = this.data;
        for (var _ = 0, C = B.length; _ < C; _++) {
            var A = B[_],
			$ = this.Ka(A);
            if ($) if (this["allowAlternating"] && _ % 2 == 1) RE($, this.JI);
            else WeL($, this.JI)
        }
    },
    setAllowAlternating: function ($) {
        if (this["allowAlternating"] != $) {
            this["allowAlternating"] = $;
            this.Dj()
        }
    },
    getAllowAlternating: function () {
        return this["allowAlternating"]
    },
    setEnableHotTrack: function ($) {
        if (this["enableHotTrack"] != $) this["enableHotTrack"] = $
    },
    getEnableHotTrack: function () {
        return this["enableHotTrack"]
    },
    setShowLoading: function ($) {
        this.showLoading = $
    },
    setAllowCellWrap: function ($) {
        if (this.allowCellWrap != $) this.allowCellWrap = $
    },
    getAllowCellWrap: function () {
        return this.allowCellWrap
    },
    setVirtualScroll: function ($) {
        if (this.virtualScroll != $) this.virtualScroll = $
    },
    getVirtualScroll: function () {
        return this.virtualScroll
    },
    setScrollTop: function ($) {
        this.scrollTop = $;
        this.$kJ.scrollTop = $
    },
    getScrollTop: function () {
        return this.$kJ.scrollTop
    },
    setBodyStyle: function ($) {
        this.bodyStyle = $;
        BIT(this.$kJ, $)
    },
    getBodyStyle: function () {
        return this.bodyStyle
    },
    setBodyCls: function ($) {
        this.bodyCls = $;
        RE(this.$kJ, $)
    },
    getBodyCls: function () {
        return this.bodyCls
    },
    setFooterStyle: function ($) {
        this.footerStyle = $;
        BIT(this.Aca, $)
    },
    getFooterStyle: function () {
        return this.footerStyle
    },
    setFooterCls: function ($) {
        this.footerCls = $;
        RE(this.Aca, $)
    },
    getFooterCls: function () {
        return this.footerCls
    },
    setShowHeader: function ($) {
        this.showHeader = $;
        this.YTFfRows();
        this["doLayout"]()
    },
    setShowFooter: function ($) {
        this["showFooter"] = $;
        this.YTFfRows();
        this["doLayout"]()
    },
    setAutoHideRowDetail: function ($) {
        this.autoHideRowDetail = $
    },
    setSortMode: function ($) {
        this.sortMode = $
    },
    getSortMode: function () {
        return this.sortMode
    },
    setAllowSortColumn: function ($) {
        this["allowSortColumn"] = $
    },
    getAllowSortColumn: function () {
        return this["allowSortColumn"]
    },
    setAllowMoveColumn: function ($) {
        this["allowMoveColumn"] = $
    },
    getAllowMoveColumn: function () {
        return this["allowMoveColumn"]
    },
    setAllowResizeColumn: function ($) {
        this["allowResizeColumn"] = $
    },
    getAllowResizeColumn: function () {
        return this["allowResizeColumn"]
    },
    setSelectOnLoad: function ($) {
        this.selectOnLoad = $
    },
    getSelectOnLoad: function () {
        return this.selectOnLoad
    },
    setAllowResize: function ($) {
        this["allowResize"] = $;
        this.Xj5.style.display = this["allowResize"] ? "" : "none"
    },
    getAllowResize: function () {
        return this["allowResize"]
    },
    setShowEmptyText: function ($) {
        this.showEmptyText = $
    },
    getShowEmptyText: function () {
        return this.showEmptyText
    },
    setEmptyText: function ($) {
        this["emptyText"] = $
    },
    getEmptyText: function () {
        return this["emptyText"]
    },
    setCellEditAction: function ($) {
        this.cellEditAction = $
    },
    getCellEditAction: function () {
        return this.cellEditAction
    },
    setAllowCellValid: function ($) {
        this.allowCellValid = $
    },
    getAllowCellValid: function () {
        return this.allowCellValid
    },
    _SX1V: true,
    showAllRowDetail: function () {
        this._SX1V = false;
        for (var $ = 0, A = this.data.length; $ < A; $++) {
            var _ = this.data[$];
            this["showRowDetail"](_)
        }
        this._SX1V = true;
        this["doLayout"]()
    },
    hideAllRowDetail: function () {
        this._SX1V = false;
        for (var $ = 0, A = this.data.length; $ < A; $++) {
            var _ = this.data[$];
            if (this["isShowRowDetail"](_)) this["hideRowDetail"](_)
        }
        this._SX1V = true;
        this["doLayout"]()
    },
    showRowDetail: function (_) {
        _ = this["getRow"](_);
        if (!_) return;
        var B = this["getRowDetailEl"](_);
        B.style.display = "";
        _._showDetail = true;
        var $ = this.Ka(_);
        RE($, "mini-grid-expandRow");
        this.fire("showrowdetail", {
            record: _
        });
        if (this._SX1V) this["doLayout"]();
        var A = this
    },
    hideRowDetail: function (_) {
        var B = this.DOA(_),
		A = document.getElementById(B);
        if (A) A.style.display = "none";
        delete _._showDetail;
        var $ = this.Ka(_);
        WeL($, "mini-grid-expandRow");
        this.fire("hiderowdetail", {
            record: _
        });
        if (this._SX1V) this["doLayout"]()
    },
    toggleRowDetail: function ($) {
        $ = this["getRow"]($);
        if (!$) return;
        if (grid["isShowRowDetail"]($)) grid["hideRowDetail"]($);
        else grid["showRowDetail"]($)
    },
    isShowRowDetail: function ($) {
        $ = this["getRow"]($);
        if (!$) return false;
        return !!$._showDetail
    },
    getRowDetailEl: function ($) {
        $ = this["getRow"]($);
        if (!$) return null;
        var A = this.DOA($),
		_ = document.getElementById(A);
        if (!_) _ = this.As6($);
        return _
    },
    getRowDetailCellEl: function ($) {
        var _ = this["getRowDetailEl"]($);
        if (_) return _.cells[0]
    },
    As6: function ($) {
        var A = this.Ka($),
		B = this.DOA($),
		_ = this["getBottomColumns"]().length;
        jQuery(A).after("<tr id=\"" + B + "\" class=\"mini-grid-detailRow\"><td class=\"mini-grid-detailCell\" colspan=\"" + _ + "\"></td></tr>");
        this.B58();
        return document.getElementById(B)
    },
    PVuJ: function () {
        var D = this._bodyInnerEl.firstChild.getElementsByTagName("tr")[0],
		B = D.getElementsByTagName("td"),
		A = 0;
        for (var _ = 0, C = B.length; _ < C; _++) {
            var $ = B[_];
            if ($.style.display != "none") A++
        }
        return A
    },
    B58: function () {
        var _ = jQuery(".mini-grid-detailRow", this.el),
		B = this.PVuJ();
        for (var A = 0, C = _.length; A < C; A++) {
            var D = _[A],
			$ = D.firstChild;
            $.colSpan = B
        }
    },
    TlY: function () {
        for (var $ = 0, B = this.data.length; $ < B; $++) {
            var _ = this.data[$];
            if (_._showDetail == true) {
                var C = this.DOA(_),
				A = document.getElementById(C);
                if (A) mini.layout(A)
            }
        }
    },
    S05: function () {
        for (var $ = 0, B = this.data.length; $ < B; $++) {
            var _ = this.data[$];
            if (_._editing == true) {
                var A = this.Ka(_);
                if (A) mini.layout(A)
            }
        }
    },
    VY0: function ($) {
        $.cancel = true;
        this.gotoPage($.pageIndex, $["pageSize"])
    },
    setSizeList: function ($) {
        if (!mini.isArray($)) return;
        this.pager.setSizeList($)
    },
    getSizeList: function () {
        return this.pager.getSizeList()
    },
    setPageSize: function ($) {
        $ = parseInt($);
        if (isNaN($)) return;
        this["pageSize"] = $;
        if (this.pager) this.pager.update(this.pageIndex, this.pageSize, this["totalCount"])
    },
    getPageSize: function () {
        return this["pageSize"]
    },
    setPageIndex: function ($) {
        $ = parseInt($);
        if (isNaN($)) return;
        this["pageIndex"] = $;
        if (this.pager) this.pager.update(this.pageIndex, this.pageSize, this["totalCount"])
    },
    getPageIndex: function () {
        return this["pageIndex"]
    },
    setShowPageSize: function ($) {
        this.showPageSize = $;
        this.pager.setShowPageSize($)
    },
    getShowPageSize: function () {
        return this.showPageSize
    },
    setShowPageIndex: function ($) {
        this.showPageIndex = $;
        this.pager.setShowPageIndex($)
    },
    getShowPageIndex: function () {
        return this.showPageIndex
    },
    setShowTotalCount: function ($) {
        this.showTotalCount = $;
        this.pager.setShowTotalCount($)
    },
    getShowTotalCount: function () {
        return this.showTotalCount
    },
    pageIndex: 0,
    pageSize: 10,
    totalCount: 0,
    totalPage: 0,
    showPageSize: true,
    showPageIndex: true,
    showTotalCount: true,
    setTotalCount: function ($) {
        this["totalCount"] = $;
        this.pager.setTotalCount($)
    },
    getTotalCount: function () {
        return this["totalCount"]
    },
    getTotalPage: function () {
        return this.totalPage
    },
    sortField: "",
    sortOrder: "",
    url: "",
    autoLoad: false,
    loadParams: null,
    ajaxAsync: true,
    ajaxMethod: "post",
    showLoading: true,
    resultAsData: false,
    checkSelectOnLoad: true,
    setCheckSelectOnLoad: function ($) {
        this["checkSelectOnLoad"] = $
    },
    getCheckSelectOnLoad: function () {
        return this["checkSelectOnLoad"]
    },
    JIRe: "total",
    _dataField: "data",
    WWiO: function ($) {
        return $.data
    },
    Sc: function (_, B, C) {
        _ = _ || {};
        if (mini.isNull(_["pageIndex"])) _["pageIndex"] = 0;
        if (mini.isNull(_["pageSize"])) _["pageSize"] = this["pageSize"];
        _.sortField = this.sortField;
        _.sortOrder = this.sortOrder;
        if (this.sortMode != "server") {
            _.sortField = this.sortField = "";
            _.sortOrder = this.sortOrder = ""
        }
        this.loadParams = _;
        if (this.showLoading) this.loading();
        var A = this.url,
		E = this.ajaxMethod;
        if (A) if (A.indexOf(".txt") != -1 || A.indexOf(".json") != -1) E = "get";
        var D = {
            url: A,
            async: this.ajaxAsync,
            type: E,
            params: _,
            cancel: false
        };
        this.fire("beforeload", D);
        if (D.cancel == true) return;
        this.RrPValue = this.RrP ? this.RrP[this.idField] : null;
        var $ = this;
        this.AJy = jQuery.ajax({
            url: D.url,
            async: D.async,
            data: D.params,
            type: D.type,
            cache: false,
            dataType: "text",
            success: function (F, D, C) {
                var J = null;
                try {
                    J = mini.decode(F)
                } catch (K) {
                    throw new Error("datagrid json is error!")
                }
                if (J == null) J = {
                    data: [],
                    total: 0
                };
                $.unmask();
                if (mini.isNumber(J.error) && J.error != 0) {
                    var L = {
                        errorCode: J.error,
                        xmlHttp: C,
                        errorMsg: J.errorMsg,
                        result: J
                    };
                    $.fire("loaderror", L);
                    return
                }
                if ($["resultAsData"] || mini.isArray(J)) {
                    var G = {};
                    G[$.JIRe] = J.length;
                    G.data = J;
                    J = G
                }
                var E = parseInt(J[$.JIRe]),
				I = $.WWiO(J);
                if (mini.isNumber(_["pageIndex"])) $["pageIndex"] = _["pageIndex"];
                if (mini.isNumber(_["pageSize"])) $["pageSize"] = _["pageSize"];
                if (mini.isNumber(E)) $["totalCount"] = E;
                var K = {
                    result: J,
                    data: I,
                    total: E,
                    cancel: false
                };
                $.fire("preload", K);
                if (K.cancel == true) return;
                var H = $.SX1V;
                $.SX1V = false;
                $["loadData"](K.data);
                if ($.RrPValue && $["checkSelectOnLoad"]) {
                    var A = $["getRowByValue"]($.RrPValue);
                    if (A) $["select"](A);
                    else $["deselectAll"]()
                } else if ($.RrP) $["deselectAll"]();
                if ($["getSelected"]() == null && $.selectOnLoad && $.data.length > 0) $["select"](0);
                if ($.collapseGroupOnLoad) $.collapseGroups();
                $.fire("load", K);
                if (B) B["call"]($, J);
                $.SX1V = H;
                $["doLayout"]()
            },
            error: function (_, B, A) {
                if (C) C["call"](scope, _);
                var D = {
                    xmlHttp: _,
                    errorMsg: _.responseText,
                    errorCode: B
                };
                $.fire("loaderror", D);
                $.unmask()
            }
        })
    },
    load: function (_, A, B) {
        if (this._loadTimer) clearTimeout(this._loadTimer);
        var $ = this;
        this["cancelEdit"]();
        this.loadParams = _ || {};
        if (this.ajaxAsync) this._loadTimer = setTimeout(function () {
            $.Sc(_, A, B)
        },
		1);
        else $.Sc(_, A, B)
    },
    reload: function (_, $) {
        this.load(this.loadParams, _, $)
    },
    gotoPage: function ($, A) {
        var _ = this.loadParams || {};
        if (mini.isNumber($)) _["pageIndex"] = $;
        if (mini.isNumber(A)) _["pageSize"] = A;
        this.load(_)
    },
    sortBy: function (E, D) {
        this.sortField = E;
        this.sortOrder = D == "asc" ? "asc" : "desc";
        if (this.sortMode == "server") {
            var A = this.loadParams || {};
            A.sortField = E;
            A.sortOrder = D;
            A["pageIndex"] = this["pageIndex"];
            this.load(A)
        } else {
            var B = this.getData().clone(),
			C = this._getSortFnByField(E);
            if (!C) return;
            var G = [];
            for (var _ = B.length - 1; _ >= 0; _--) {
                var $ = B[_],
				F = $[E];
                if (mini.isNull(F) || F === "") {
                    G.insert(0, $);
                    B.removeAt(_)
                }
            }
            mini.sort(B, C, this);
            B.insertRange(0, G);
            if (this.sortOrder == "desc") B.reverse();
            this.data = B;
            this["doUpdate"]()
        }
    },
    clearSort: function () {
        this.sortField = "";
        this.sortOrder = "";
        this.reload()
    },
    _getSortFnByField: function (D) {
        if (!D) return null;
        var F = "string",
		C = null,
		E = this["getBottomColumns"]();
        for (var $ = 0, G = E.length; $ < G; $++) {
            var A = E[$];
            if (A.field == D) {
                if (A.dataType) F = A.dataType.toLowerCase();
                break
            }
        }
        var B = mini.sortTypes[F];
        if (!B) B = mini.sortTypes["string"];
        function _(A, F) {
            var C = A[D],
			_ = F[D];
            if (mini.isNull(C) || C === "" || isNaN(C)) return -1;
            if (mini.isNull(_) || _ === "" || isNaN(_)) return 1;
            var $ = B(C),
			E = B(_);
            if ($ > E) return 1;
            else if ($ == E) return 0;
            else return -1
        }
        C = _;
        return C
    },
    allowCellSelect: false,
    allowCellEdit: false,
    Av6: "mini-grid-cell-selected",
    Do: null,
    HCM: null,
    KVo: null,
    Au: null,
    Gge: function (B) {
        if (this.Do) {
            var $ = this.Do[0],
			A = this.Do[1],
			_ = this.Fmaf($, A);
            if (_) if (B) RE(_, this.Av6);
            else WeL(_, this.Av6)
        }
    },
    setCurrentCell: function ($) {
        if (this.Do != $) {
            this.Gge(false);
            this.Do = $;
            this.Gge(true);
            if ($) this["scrollIntoView"]($[0], $[1]);
            this.fire("currentcellchanged")
        }
    },
    getCurrentCell: function () {
        var $ = this.Do;
        if ($) if (this.data.indexOf($[0]) == -1) {
            this.Do = null;
            $ = null
        }
        return $
    },
    setAllowCellSelect: function ($) {
        this["allowCellSelect"] = $
    },
    getAllowCellSelect: function ($) {
        return this["allowCellSelect"]
    },
    setAllowCellEdit: function ($) {
        this["allowCellEdit"] = $
    },
    getAllowCellEdit: function ($) {
        return this["allowCellEdit"]
    },
    beginEditCell: function ($, A) {
        var _ = [$, A];
        if ($ && _) grid["setCurrentCell"](_);
        _ = this.getCurrentCell();
        if (this.HCM && _) if (this.HCM[0] == _[0] && this.HCM[1] == _[1]) return;
        if (this.HCM) this.commitEdit();
        if (_) {
            var $ = _[0],
			A = _[1],
			B = this.M6O($, A, this["getCellEditor"](A));
            if (B !== false) {
                this.HCM = _;
                this.WU($, A)
            }
        }
    },
    cancelEdit: function () {
        if (this["allowCellEdit"]) {
            if (this.HCM) this.N_m8()
        } else if (this["isEditing"]()) {
            this.SX1V = false;
            var A = this.data.clone();
            for (var $ = 0, B = A.length; $ < B; $++) {
                var _ = A[$];
                if (_._editing == true) this["cancelEditRow"]($)
            }
            this.SX1V = true;
            this["doLayout"]()
        }
    },
    commitEdit: function () {
        if (this["allowCellEdit"]) {
            if (this.HCM) {
                this.Was(this.HCM[0], this.HCM[1]);
                this.N_m8()
            }
        } else if (this["isEditing"]()) {
            this.SX1V = false;
            var A = this.data.clone();
            for (var $ = 0, B = A.length; $ < B; $++) {
                var _ = A[$];
                if (_._editing == true) this["commitEditRow"]($)
            }
            this.SX1V = true;
            this["doLayout"]()
        }
    },
    getCellEditor: function (_, $) {
        _ = this["getColumn"](_);
        if (!_) return;
        if (this["allowCellEdit"]) {
            var B = mini.getAndCreate(_.editor);
            if (B && B != _.editor) _.editor = B;
            return B
        } else {
            $ = this["getRow"]($);
            _ = this["getColumn"](_);
            if (!$) $ = this.getEditingRow();
            if (!$ || !_) return null;
            var A = this.uid + "$" + $._uid + "$" + _.name + "$editor";
            return mini.get(A)
        }
    },
    M6O: function ($, A, C) {
        var B = {
            sender: this,
            rowIndex: this.data.indexOf($),
            row: $,
            record: $,
            column: A,
            field: A.field,
            editor: C,
            value: $[A.field],
            cancel: false
        };
        this.fire("cellbeginedit", B);
        var C = B.editor;
        value = B.value;
        if (B.cancel) return false;
        if (!C) return false;
        if (mini.isNull(value)) value = "";
        if (C["setValue"]) C["setValue"](value);
        C.ownerRowID = $._uid;
        if (A.displayField && C["setText"]) {
            var _ = $[A.displayField];
            C["setText"](_)
        }
        if (this["allowCellEdit"]) this.KVo = B.editor;
        return true
    },
    Was: function (_, B, A, D) {
        var C = {
            sender: this,
            record: _,
            row: _,
            column: B,
            field: B.field,
            editor: D ? D : this["getCellEditor"](B),
            value: mini.isNull(A) ? "" : A,
            text: "",
            cancel: false
        };
        if (C.editor && C.editor.getValue) C.value = C.editor.getValue();
        if (C.editor && C.editor.getText) C.text = C.editor.getText();
        this.fire("cellcommitedit", C);
        if (C.cancel == false) if (this["allowCellEdit"]) {
            var $ = {};
            $[B.field] = C.value;
            if (B.displayField) $[B.displayField] = C.text;
            this["updateRow"](_, $)
        }
        return C
    },
    N_m8: function () {
        if (!this.HCM) return;
        var _ = this.HCM[0],
		C = this.HCM[1],
		E = {
		    sender: this,
		    record: _,
		    row: _,
		    column: C,
		    field: C.field,
		    editor: this.KVo,
		    value: _[C.field]
		};
        this.fire("cellendedit", E);
        if (this["allowCellEdit"]) {
            var D = E.editor;
            if (D && D.setIsValid) D.setIsValid(true);
            if (this.Au) this.Au.style.display = "none";
            var A = this.Au.childNodes;
            for (var $ = A.length - 1; $ >= 0; $--) {
                var B = A[$];
                this.Au.removeChild(B)
            }
            if (D && D["hidePopup"]) D["hidePopup"]();
            if (D && D["setValue"]) D["setValue"]("");
            this.KVo = null;
            this.HCM = null;
            if (this.allowCellValid) this.validateCell(_, C)
        }
    },
    WU: function (_, C) {
        if (!this.KVo) return false;
        var $ = this["getCellBox"](_, C),
		E = {
		    sender: this,
		    record: _,
		    row: _,
		    column: C,
		    field: C.field,
		    cellBox: $,
		    editor: this.KVo
		};
        this.fire("cellshowingedit", E);
        var D = E.editor;
        if (D && D.setIsValid) D.setIsValid(true);
        var B = this.N5W($);
        this.Au.style.zIndex = mini.getMaxZIndex();
        if (D["render"]) {
            D["render"](this.Au);
            setTimeout(function () {
                D.focus();
                if (D["selectText"]) D["selectText"]()
            },
			10);
            if (D["setVisible"]) D["setVisible"](true)
        } else if (D.el) {
            this.Au.appendChild(D.el);
            setTimeout(function () {
                try {
                    D.el.focus()
                } catch ($) { }
            },
			10)
        }
        if (D["setWidth"]) {
            var A = $.width;
            if (A < 50) A = 50;
            D["setWidth"](A)
        }
        VNV(document, "mousedown", this.LzKR, this);
        if (C.autoShowPopup && D["showPopup"]) D["showPopup"]()
    },
    LzKR: function (C) {
        if (this.KVo) {
            var A = this.W6(C);
            if (this.HCM && A) if (this.HCM[0] == A.record && this.HCM[1] == A.column) return false;
            var _ = false;
            if (this.KVo["within"]) _ = this.KVo["within"](C);
            else _ = Yma(this.Au, C.target);
            if (_ == false) {
                var B = this;
                if (Yma(this.$kJ, C.target) == false) setTimeout(function () {
                    B.commitEdit()
                },
				1);
                else {
                    var $ = B.HCM;
                    setTimeout(function () {
                        var _ = B.HCM;
                        if ($ == _) B.commitEdit()
                    },
					70)
                }
                BP(document, "mousedown", this.LzKR, this)
            }
        }
    },
    N5W: function ($) {
        if (!this.Au) {
            this.Au = mini.append(document.body, "<div class=\"mini-grid-editwrap\" style=\"position:absolute;\"></div>");
            VNV(this.Au, "keydown", this.ADY, this)
        }
        this.Au.style.zIndex = 1000000000;
        this.Au.style.display = "block";
        mini["setXY"](this.Au, $.x, $.y);
        SI9N(this.Au, $.width);
        return this.Au
    },
    ADY: function (A) {
        var _ = this.KVo;
        if (A.keyCode == 13 && A.ctrlKey == false && _ && _.type == "textarea") return;
        if (A.keyCode == 13) {
            var $ = this.HCM;
            if ($ && $[1] && $[1].enterCommit === false) return;
            this.commitEdit();
            this.focus()
        } else if (A.keyCode == 27) {
            this["cancelEdit"]();
            this.focus()
        } else if (A.keyCode == 9) this["cancelEdit"]()
    },
    getEditorOwnerRow: function (_) {
        var $ = _.ownerRowID;
        return this.getRowByUID($)
    },
    beginEditRow: function (row) {
        if (this["allowCellEdit"]) return;
        var sss = new Date();
        row = this["getRow"](row);
        if (!row) return;
        var rowEl = this.Ka(row);
        if (!rowEl) return;
        row._editing = true;
        var s = this.R7SB(row),
		rowEl = this.Ka(row);
        jQuery(rowEl).before(s);
        rowEl.parentNode.removeChild(rowEl);
        rowEl = this.Ka(row);
        RE(rowEl, "mini-grid-rowEdit");
        var columns = this["getBottomColumns"]();
        for (var i = 0, l = columns.length; i < l; i++) {
            var column = columns[i],
			value = row[column.field],
			cellId = this.YVUE(row, columns[i]),
			cellEl = document.getElementById(cellId);
            if (!cellEl) continue;
            if (typeof column.editor == "string") column.editor = eval("(" + column.editor + ")");
            var editorConfig = mini.copyTo({},
			column.editor);
            editorConfig.id = this.uid + "$" + row._uid + "$" + column.name + "$editor";
            var editor = mini.create(editorConfig);
            if (this.M6O(row, column, editor)) if (editor) {
                RE(cellEl, "mini-grid-cellEdit");
                cellEl.innerHTML = "";
                cellEl.appendChild(editor.el);
                RE(editor.el, "mini-grid-editor")
            }
        }
        this["doLayout"]()
    },
    cancelEditRow: function (B) {
        if (this["allowCellEdit"]) return;
        B = this["getRow"](B);
        if (!B || !B._editing) return;
        delete B._editing;
        var _ = this.Ka(B),
		D = this["getBottomColumns"]();
        for (var $ = 0, F = D.length; $ < F; $++) {
            var C = D[$],
			H = this.YVUE(B, D[$]),
			A = document.getElementById(H),
			E = A.firstChild,
			I = mini.get(E);
            if (!I) continue;
            I["destroy"]()
        }
        var G = this.R7SB(B);
        jQuery(_).before(G);
        _.parentNode.removeChild(_);
        this["doLayout"]()
    },
    commitEditRow: function ($) {
        if (this["allowCellEdit"]) return;
        $ = this["getRow"]($);
        if (!$ || !$._editing) return;
        var _ = this["getEditRowData"]($);
        this.BV65 = false;
        this["updateRow"]($, _);
        this.BV65 = true;
        this["cancelEditRow"]($)
    },
    isEditing: function () {
        for (var $ = 0, A = this.data.length; $ < A; $++) {
            var _ = this.data[$];
            if (_._editing == true) return true
        }
        return false
    },
    isEditingRow: function ($) {
        $ = this["getRow"]($);
        if (!$) return false;
        return !!$._editing
    },
    isNewRow: function ($) {
        return $._state == "added"
    },
    getEditingRows: function () {
        var A = [];
        for (var $ = 0, B = this.data.length; $ < B; $++) {
            var _ = this.data[$];
            if (_._editing == true) A.push(_)
        }
        return A
    },
    getEditingRow: function () {
        var $ = this.getEditingRows();
        return $[0]
    },
    getEditData: function (C) {
        var B = [];
        for (var $ = 0, D = this.data.length; $ < D; $++) {
            var _ = this.data[$];
            if (_._editing == true) {
                var A = this["getEditRowData"]($, C);
                A._index = $;
                B.push(A)
            }
        }
        return B
    },
    getEditRowData: function (G, I) {
        G = this["getRow"](G);
        if (!G || !G._editing) return null;
        var H = {},
		B = this["getBottomColumns"]();
        for (var F = 0, C = B.length; F < C; F++) {
            var A = B[F],
			D = this.YVUE(G, B[F]),
			_ = document.getElementById(D),
			J = _.firstChild,
			E = mini.get(J);
            if (!E) continue;
            var K = this.Was(G, A, null, E);
            H[A.field] = K.value;
            if (A.displayField) H[A.displayField] = K.text
        }
        H[this.idField] = G[this.idField];
        if (I) {
            var $ = mini.copyTo({},
			G);
            H = mini.copyTo($, H)
        }
        return H
    },
    getChanges: function (B) {
        var A = [];
        if (!B || B == "removed") A.addRange(this.Q_J);
        for (var $ = 0, C = this.data.length; $ < C; $++) {
            var _ = this.data[$];
            if (_._state && (!B || B == _._state)) A.push(_)
        }
        return A
    },
    isChanged: function () {
        var $ = this.getChanges();
        return $.length > 0
    },
    IsQW: "_uid",
    $LG: function ($) {
        var A = $[this.IsQW],
		_ = this.U$w[A];
        if (!_) _ = this.U$w[A] = {};
        return _
    },
    Hn6: function (A, _) {
        var $ = this.U$w[A[this.IsQW]];
        if (!$) return false;
        if (mini.isNull(_)) return false;
        return $.hasOwnProperty(_)
    },
    S8F: function (A, B) {
        var E = false;
        for (var C in B) {
            var $ = B[C],
			D = A[C];
            if (mini["isEquals"](D, $)) continue;
            A[C] = $;
            if (A._state != "added") {
                A._state = "modified";
                var _ = this.$LG(A);
                if (!_.hasOwnProperty(C)) _[C] = D
            }
            E = true
        }
        return E
    },
    BV65: true,
    updateRow: function (B, C, A) {
        B = this["getRow"](B);
        if (!B || !C) return;
        if (typeof C == "string") {
            var $ = {};
            $[C] = A;
            C = $
        }
        var E = this.S8F(B, C);
        if (E == false) return;
        if (this.BV65) {
            var D = this,
			F = D.R7SB(B),
			_ = D.Ka(B);
            jQuery(_).before(F);
            _.parentNode.removeChild(_)
        }
        if (B._state == "modified") this.fire("updaterow", {
            record: B,
            row: B
        });
        if (B == this["getSelected"]()) this._aW(B);
        this.Mso()
    },
    deleteRows: function (_) {
        if (!mini.isArray(_)) return;
        _ = _.clone();
        for (var $ = 0, A = _.length; $ < A; $++) this.deleteRow(_[$])
    },
    deleteRow: function (_) {
        _ = this["getRow"](_);
        if (!_ || _._state == "deleted") return;
        if (_._state == "added") this["removeRow"](_, true);
        else {
            if (this.isEditingRow(_)) this["cancelEditRow"](_);
            _._state = "deleted";
            var $ = this.Ka(_);
            RE($, "mini-grid-deleteRow");
            this.fire("deleterow", {
                record: _,
                row: _
            })
        }
    },
    removeRows: function (_, B) {
        if (!mini.isArray(_)) return;
        _ = _.clone();
        for (var $ = 0, A = _.length; $ < A; $++) this["removeRow"](_[$], B)
    },
    removeSelected: function () {
        var $ = this["getSelected"]();
        if ($) this["removeRow"]($, true)
    },
    removeRow: function (A, H) {
        A = this["getRow"](A);
        if (!A) return;
        var D = A == this["getSelected"](),
		C = this["isSelected"](A),
		$ = this.data.indexOf(A);
        this.data.remove(A);
        if (A._state != "added") {
            A._state = "removed";
            this.Q_J.push(A);
            delete this.U$w[A[this.IsQW]]
        }
        delete this.Cb[A._uid];
        var G = this.R7SB(A),
		_ = this.Ka(A);
        if (_) _.parentNode.removeChild(_);
        var F = this.DOA(A),
		E = document.getElementById(F);
        if (E) E.parentNode.removeChild(E);
        if (C && H) {
            var B = this.getAt($);
            if (!B) B = this.getAt($ - 1);
            this["deselectAll"]();
            this["select"](B)
        }
        this.JVD();
        this.fire("removerow", {
            record: A,
            row: A
        });
        if (D) this._aW(A);
        this.Dj();
        this.Mso()
    },
    autoCreateNewID: false,
    addRows: function (A, $) {
        if (!mini.isArray(A)) return;
        A = A.clone();
        for (var _ = 0, B = A.length; _ < B; _++) this.addRow(A[_], $)
    },
    addRow: function (A, $) {
        if (mini.isNull($)) $ = this.data.length;
        $ = this.indexOf($);
        var B = this["getRow"]($);
        this.data.insert($, A);
        if (!A[this.idField]) {
            if (this.autoCreateNewID) A[this.idField] = UUID();
            var D = {
                row: A,
                record: A
            };
            this.fire("beforeaddrow", D)
        }
        A._state = "added";
        delete this.Cb[A._uid];
        A._uid = H$L++;
        this.Cb[A._uid] = A;
        var C = this.R7SB(A);
        if (B) {
            var _ = this.Ka(B);
            jQuery(_).before(C)
        } else mini.append(this._bodyInnerEl.firstChild, C);
        this.Dj();
        this.Mso();
        this.fire("addrow", {
            record: A,
            row: A
        })
    },
    moveRow: function (B, _) {
        B = this["getRow"](B);
        if (!B) return;
        if (_ < 0) return;
        if (_ > this.data.length) return;
        var D = this["getRow"](_);
        if (B == D) return;
        this.data.remove(B);
        var A = this.Ka(B);
        if (D) {
            _ = this.data.indexOf(D);
            this.data.insert(_, B);
            var C = this.Ka(D);
            jQuery(C).before(A)
        } else {
            this.data.insert(this.data.length, B);
            var $ = this._bodyInnerEl.firstChild;
            mini.append($.firstChild || $, A)
        }
        this.Dj();
        this.Mso();
        this["scrollIntoView"](B);
        this.fire("moverow", {
            record: B,
            row: B,
            index: _
        })
    },
    clearRows: function () {
        this.data = [];
        this["doUpdate"]()
    },
    indexOf: function ($) {
        if (typeof $ == "number") return $;
        return this.data.indexOf($)
    },
    getAt: function ($) {
        return this.data[$]
    },
    getRow: function ($) {
        var _ = typeof $;
        if (_ == "number") return this.data[$];
        else if (_ == "object") return $
    },
    getRowByValue: function (A) {
        for (var _ = 0, B = this.data.length; _ < B; _++) {
            var $ = this.data[_];
            if ($[this.idField] == A) return $
        }
    },
    getRowByUID: function ($) {
        return this.Cb[$]
    },
    findRows: function (C) {
        var A = [];
        if (C) for (var $ = 0, B = this.data.length; $ < B; $++) {
            var _ = this.data[$];
            if (C(_) === true) A.push(_)
        }
        return A
    },
    findRow: function (B) {
        if (B) for (var $ = 0, A = this.data.length; $ < A; $++) {
            var _ = this.data[$];
            if (B(_) === true) return _
        }
    },
    collapseGroupOnLoad: false,
    setCollapseGroupOnLoad: function ($) {
        this.collapseGroupOnLoad = $
    },
    getCollapseGroupOnLoad: function () {
        return this.collapseGroupOnLoad
    },
    showGroupSummary: false,
    setShowGroupSummary: function ($) {
        this.showGroupSummary = $
    },
    getShowGroupSummary: function () {
        return this.showGroupSummary
    },
    collapseGroups: function () {
        if (!this.Ie) return;
        for (var $ = 0, A = this.Ie.length; $ < A; $++) {
            var _ = this.Ie[$];
            this.O49C(_)
        }
    },
    expandGroups: function () {
        if (!this.Ie) return;
        for (var $ = 0, A = this.Ie.length; $ < A; $++) {
            var _ = this.Ie[$];
            this._YL(_)
        }
    },
    O49C: function (A) {
        var C = A.rows;
        for (var _ = 0, E = C.length; _ < E; _++) {
            var B = C[_],
			$ = this.Ka(B);
            if ($) $.style.display = "none"
        }
        A.expanded = false;
        var F = this.uid + "$group$" + A.id,
		D = document.getElementById(F);
        if (D) RE(D, "mini-grid-group-collapse");
        this["doLayout"]()
    },
    _YL: function (A) {
        var C = A.rows;
        for (var _ = 0, E = C.length; _ < E; _++) {
            var B = C[_],
			$ = this.Ka(B);
            if ($) $.style.display = ""
        }
        A.expanded = true;
        var F = this.uid + "$group$" + A.id,
		D = document.getElementById(F);
        if (D) WeL(D, "mini-grid-group-collapse");
        this["doLayout"]()
    },
    G37: 1,
    MZ$: "",
    Brrj: "",
    groupBy: function ($, _) {
        if (!$) return;
        this.MZ$ = $;
        if (typeof _ == "string") _ = _.toLowerCase();
        this.Brrj = _;
        this.Ie = null;
        this["doUpdate"]()
    },
    clearGroup: function () {
        this.MZ$ = "";
        this.Brrj = "";
        this.Ie = null;
        this["doUpdate"]()
    },
    getGroupField: function () {
        return this.MZ$
    },
    getGroupDir: function () {
        return this.Brrj
    },
    isGrouping: function () {
        return this.MZ$ != ""
    },
    HCQT: function () {
        if (this["isGrouping"]() == false) return null;
        this.Ie = null;
        if (!this.Ie) {
            var F = this.MZ$,
			H = this.Brrj,
			D = this.data.clone();
            if (typeof H == "function") mini.sort(D, H);
            else {
                mini.sort(D,
				function (_, B) {
				    var $ = _[F],
					A = B[F];
				    if ($ > A) return 1;
				    else return 0
				},
				this);
                if (H == "desc") D.resvert()
            }
            var B = [],
			C = {};
            for (var _ = 0, G = D.length; _ < G; _++) {
                var $ = D[_],
				I = $[F],
				E = mini.isDate(I) ? I.getTime() : I,
				A = C[E];
                if (!A) {
                    A = C[E] = {};
                    A.field = F,
					A.dir = H;
                    A.value = I;
                    A.rows = [];
                    B.push(A);
                    A.id = this.G37++
                }
                A.rows.push($)
            }
            this.Ie = B
        }
        return this.Ie
    },
    XEQ: function (C) {
        if (!this.Ie) return null;
        var A = this.Ie;
        for (var $ = 0, B = A.length; $ < B; $++) {
            var _ = A[$];
            if (_.id == C) return _
        }
    },
    Lsvo: function ($) {
        var _ = {
            group: $,
            rows: $.rows,
            field: $.field,
            dir: $.dir,
            value: $.value,
            cellHtml: $.field + " :" + $.value
        };
        this.fire("drawgroup", _);
        return _
    },
    onDrawGroupHeader: function (_, $) {
        this.on("drawgroupheader", _, $)
    },
    onDrawGroupSummary: function (_, $) {
        this.on("drawgroupsummary", _, $)
    },
    margeCells: function (F) {
        if (!mini.isArray(F)) return;
        for (var $ = 0, D = F.length; $ < D; $++) {
            var B = F[$];
            if (!B.rowSpan) B.rowSpan = 1;
            if (!B.colSpan) B.colSpan = 1;
            var E = this.F3(B.rowIndex, B.columnIndex, B.rowSpan, B.colSpan);
            for (var C = 0, _ = E.length; C < _; C++) {
                var A = E[C];
                if (C != 0) A.style.display = "none";
                else {
                    A.rowSpan = B.rowSpan;
                    A.colSpan = B.colSpan
                }
            }
        }
    },
    F3: function (I, E, A, B) {
        var J = [];
        if (!mini.isNumber(I)) return [];
        if (!mini.isNumber(E)) return [];
        var C = this["getBottomColumns"](),
		G = this.data;
        for (var F = I, D = I + A; F < D; F++) for (var H = E, $ = E + B; H < $; H++) {
            var _ = this.Fmaf(F, H);
            if (_) J.push(_)
        }
        return J
    },
    RrP: null,
    ABm9: [],
    JVD: function () {
        var A = this.ABm9;
        for (var $ = A.length - 1; $ >= 0; $--) {
            var _ = A[$];
            if (!!this.Cb[_._uid] == false) {
                A.removeAt($);
                delete this.NvM[_._uid]
            }
        }
        if (this.RrP) if (!!this.NvM[this.RrP._uid] == false) this.RrP = null
    },
    setAllowRowSelect: function ($) {
        this["allowRowSelect"] = $
    },
    getAllowRowSelect: function ($) {
        return this["allowRowSelect"]
    },
    setMultiSelect: function ($) {
        if (this["multiSelect"] != $) {
            this["multiSelect"] = $;
            this.Uq$()
        }
    },
    isSelected: function ($) {
        $ = this["getRow"]($);
        if (!$) return false;
        return !!this.NvM[$._uid]
    },
    getSelecteds: function () {
        this.JVD();
        return this.ABm9.clone()
    },
    setCurrent: function ($) {
        this["setSelected"]($)
    },
    getCurrent: function () {
        return this["getSelected"]()
    },
    getSelected: function () {
        this.JVD();
        return this.RrP
    },
    scrollIntoView: function (A, B) {
        try {
            if (B) {
                var _ = this.Fmaf(A, B);
                mini["scrollIntoView"](_, this.$kJ, true)
            } else {
                var $ = this.Ka(A);
                mini["scrollIntoView"]($, this.$kJ, false)
            }
        } catch (C) { }
    },
    setSelected: function ($) {
        if ($) this["select"]($);
        else this["deselect"](this.RrP);
        if (this.RrP) this["scrollIntoView"](this.RrP);
        this.U0s()
    },
    select: function ($) {
        $ = this["getRow"]($);
        if (!$) return;
        this.RrP = $;
        this["selects"]([$])
    },
    deselect: function ($) {
        $ = this["getRow"]($);
        if (!$) return;
        this["deselects"]([$])
    },
    selectAll: function () {
        var $ = this.data.clone();
        this["selects"]($)
    },
    deselectAll: function () {
        var $ = this.ABm9.clone();
        this.RrP = null;
        this["deselects"]($)
    },
    clearSelect: function () {
        this["deselectAll"]()
    },
    selects: function (A) {
        if (!A || A.length == 0) return;
        A = A.clone();
        this.YQU(A, true);
        for (var _ = 0, B = A.length; _ < B; _++) {
            var $ = A[_];
            if (!this["isSelected"]($)) {
                this.ABm9.push($);
                this.NvM[$._uid] = $
            }
        }
        this.NAF()
    },
    deselects: function (A) {
        if (!A) A = [];
        A = A.clone();
        this.YQU(A, false);
        for (var _ = A.length - 1; _ >= 0; _--) {
            var $ = A[_];
            if (this["isSelected"]($)) {
                this.ABm9.remove($);
                delete this.NvM[$._uid]
            }
        }
        if (A.indexOf(this.RrP) != -1) this.RrP = null;
        this.NAF()
    },
    YQU: function (A, D) {
        var B = new Date();
        for (var _ = 0, C = A.length; _ < C; _++) {
            var $ = A[_];
            if (D) this["addRowCls"]($, this.Ct$);
            else this["removeRowCls"]($, this.Ct$)
        }
    },
    NAF: function () {
        if (this.$lU) clearTimeout(this.$lU);
        var $ = this;
        this.$lU = setTimeout(function () {
            var _ = {
                selecteds: $.getSelecteds(),
                selected: $["getSelected"]()
            };
            $.fire("SelectionChanged", _);
            $._aW(_.selected)
        },
		1)
    },
    _aW: function ($) {
        if (this._currentTimer) clearTimeout(this._currentTimer);
        var _ = this;
        this._currentTimer = setTimeout(function () {
            var A = {
                record: $,
                row: $
            };
            _.fire("CurrentChanged", A);
            _._currentTimer = null
        },
		1)
    },
    addRowCls: function (_, A) {
        var $ = this.Ka(_);
        if ($) RE($, A)
    },
    removeRowCls: function (_, A) {
        var $ = this.Ka(_);
        if ($) WeL($, A)
    },
    Osh: function (_, $) {
        _ = this["getRow"](_);
        if (!_ || _ == this.W6M) return;
        var A = this.Ka(_);
        if ($ && A) this["scrollIntoView"](_);
        if (this.W6M == _) return;
        this.U0s();
        this.W6M = _;
        RE(A, this.GjJ)
    },
    U0s: function () {
        if (!this.W6M) return;
        var $ = this.Ka(this.W6M);
        if ($) WeL($, this.GjJ);
        this.W6M = null
    },
    V4c: function (B) {
        var A = MRj9(B.target, this.PgR);
        if (!A) return null;
        var $ = A.id.split("$"),
		_ = $[$.length - 1];
        return this.getRowByUID(_)
    },
    Bys: function (C, A) {
        if (this["allowCellEdit"]) this.commitEdit();
        var B = jQuery(this.$kJ).css("overflow-y");
        if (B == "hidden") {
            var $ = C.wheelDelta || -C.detail * 24,
			_ = this.$kJ.scrollTop;
            _ -= $;
            this.$kJ.scrollTop = _;
            if (_ == this.$kJ.scrollTop) C.preventDefault();
            var C = {
                scrollTop: this.$kJ.scrollTop,
                direction: "vertical"
            };
            this.fire("scroll", C)
        }
    },
    YY: function (D) {
        this._tryFocus(D);
        var A = MRj9(D.target, "mini-grid-groupRow");
        if (A) {
            var _ = A.id.split("$"),
			C = _[_.length - 1],
			$ = this.XEQ(C);
            if ($) {
                var B = !($.expanded === false ? false : true);
                if (B) this._YL($);
                else this.O49C($)
            }
        } else this.ZP(D, "Click")
    },
    _tryFocus: function ($) {
        if (Yma(this.XdU, $.target) || Yma(this.NfMd, $.target) || Yma(this.Aca, $.target) || MRj9($.target, "mini-grid-rowEdit") || MRj9($.target, "mini-grid-detailRow"));
        else this.focus()
    },
    E8S: function ($) {
        this.ZP($, "Dblclick")
    },
    UNAE: function ($) {
        this.ZP($, "MouseDown");
        this._tryFocus($)
    },
    ZXp: function ($) {
        this.ZP($, "MouseUp")
    },
    A$Z: function ($) {
        this.ZP($, "MouseMove")
    },
    Vv5: function ($) {
        this.ZP($, "MouseOver")
    },
    Gj$: function ($) {
        this.ZP($, "MouseOut")
    },
    M$: function ($) {
        this.ZP($, "KeyDown")
    },
    Y_VP: function ($) {
        this.ZP($, "KeyUp")
    },
    JZg: function ($) {
        this.ZP($, "ContextMenu")
    },
    ZP: function (F, D) {
        if (!this.enabled) return;
        var C = this.W6(F),
		_ = C.record,
		B = C.column;
        if (_) {
            var A = {
                record: _,
                row: _,
                htmlEvent: F
            },
			E = this["_OnRow" + D];
            if (E) E["call"](this, A);
            else this.fire("row" + D, A)
        }
        if (B) {
            A = {
                column: B,
                field: B.field,
                htmlEvent: F
            },
			E = this["_OnColumn" + D];
            if (E) E["call"](this, A);
            else this.fire("column" + D, A)
        }
        if (_ && B) {
            A = {
                sender: this,
                record: _,
                row: _,
                column: B,
                field: B.field,
                htmlEvent: F
            },
			E = this["_OnCell" + D];
            if (E) E["call"](this, A);
            else this.fire("cell" + D, A);
            if (B["onCell" + D]) B["onCell" + D]["call"](B, A)
        }
        if (!_ && B) {
            A = {
                column: B,
                htmlEvent: F
            },
			E = this["_OnHeaderCell" + D];
            if (E) E["call"](this, A);
            else {
                var $ = "onheadercell" + D.toLowerCase();
                if (B[$]) {
                    A.sender = this;
                    B[$](A)
                }
                this.fire("headercell" + D, A)
            }
        }
        if (!_) this.U0s()
    },
    Y2v: function ($, B, C, D) {
        var _ = $[B.field],
		E = {
		    sender: this,
		    rowIndex: C,
		    columnIndex: D,
		    record: $,
		    row: $,
		    column: B,
		    field: B.field,
		    value: _,
		    cellHtml: _,
		    rowCls: null,
		    cellCls: B.cellCls || "",
		    rowStyle: null,
		    cellStyle: B.cellStyle || "",
		    allowCellWrap: this.allowCellWrap
		};
        if (B.dateFormat) if (mini.isDate(E.value)) E.cellHtml = mini.formatDate(_, B.dateFormat);
        else E.cellHtml = _;
        if (B.displayField) E.cellHtml = $[B.displayField];
        var A = B.renderer;
        if (A) {
            fn = typeof A == "function" ? A : window[A];
            if (fn) E.cellHtml = fn["call"](B, E)
        }
        this.fire("drawcell", E);
        if (E.cellHtml === null || E.cellHtml === undefined || E.cellHtml === "") E.cellHtml = "&nbsp;";
        return E
    },
    _OnCellMouseDown: function (_) {
        var $ = _.record;
        if ($.enabled === false) return;
        this.fire("cellmousedown", _)
    },
    _OnRowMouseOut: function ($) {
        if (!this.enabled) return;
        if (Yma(this.el, $.target)) return
    },
    _OnRowMouseMove: function (_) {
        record = _.record;
        if (!this.enabled || record.enabled === false || this["enableHotTrack"] == false) return;
        this.fire("rowmousemove", _);
        var $ = this;
        $.Osh(record)
    },
    _OnHeaderCellClick: function (A) {
        A.sender = this;
        var $ = A.column;
        if (!D4ge(A.htmlEvent.target, "mini-grid-splitter")) {
            if (this["allowSortColumn"] && this["isEditing"]() == false) if (!$.columns || $.columns.length == 0) if ($.field && $.allowSort !== false) {
                var _ = "asc";
                if (this.sortField == $.field) _ = this.sortOrder == "asc" ? "desc" : "asc";
                this.sortBy($.field, _)
            }
            this.fire("headercellclick", A)
        }
    },
    __OnHtmlContextMenu: function (_) {
        var $ = {
            popupEl: this.el,
            htmlEvent: _,
            cancel: false
        };
        if (Yma(this.VEE, _.target)) {
            if (this.headerContextMenu) {
                this.headerContextMenu.fire("BeforeOpen", $);
                if ($.cancel == true) return;
                this.headerContextMenu.fire("opening", $);
                if ($.cancel == true) return;
                this.headerContextMenu.showAtPos(_.pageX, _.pageY);
                this.headerContextMenu.fire("Open", $)
            }
        } else if (this["contextMenu"]) {
            this["contextMenu"].fire("BeforeOpen", $);
            if ($.cancel == true) return;
            this["contextMenu"].fire("opening", $);
            if ($.cancel == true) return;
            this["contextMenu"].showAtPos(_.pageX, _.pageY);
            this["contextMenu"].fire("Open", $)
        }
        return false
    },
    headerContextMenu: null,
    setHeaderContextMenu: function ($) {
        var _ = this._getContextMenu($);
        if (!_) return;
        if (this.headerContextMenu !== _) {
            this.headerContextMenu = _;
            this.headerContextMenu.owner = this;
            VNV(this.el, "contextmenu", this.__OnHtmlContextMenu, this)
        }
    },
    getHeaderContextMenu: function () {
        return this.headerContextMenu
    },
    onRowDblClick: function (_, $) {
        this.on("rowdblclick", _, $)
    },
    onRowClick: function (_, $) {
        this.on("rowclick", _, $)
    },
    onRowMouseDown: function (_, $) {
        this.on("rowmousedown", _, $)
    },
    onRowContextMenu: function (_, $) {
        this.on("rowcontextmenu", _, $)
    },
    onCellClick: function (_, $) {
        this.on("cellclick", _, $)
    },
    onCellMouseDown: function (_, $) {
        this.on("cellmousedown", _, $)
    },
    onCellContextMenu: function (_, $) {
        this.on("cellcontextmenu", _, $)
    },
    onBeforeLoad: function (_, $) {
        this.on("beforeload", _, $)
    },
    onLoad: function (_, $) {
        this.on("load", _, $)
    },
    onLoadError: function (_, $) {
        this.on("loaderror", _, $)
    },
    onPreLoad: function (_, $) {
        this.on("preload", _, $)
    },
    onDrawCell: function (_, $) {
        this.on("drawcell", _, $)
    },
    onCellBeginEdit: function (_, $) {
        this.on("cellbeginedit", _, $)
    },
    getAttrs: function (el) {
        var attrs = mini.DataGrid["superclass"]["getAttrs"]["call"](this, el),
		cs = mini["getChildNodes"](el);
        for (var i = 0, l = cs.length; i < l; i++) {
            var node = cs[i],
			property = jQuery(node).attr("property");
            if (!property) continue;
            property = property.toLowerCase();
            if (property == "columns") attrs.columns = mini._ParseColumns(node);
            else if (property == "data") attrs.data = node.innerHTML
        }
        mini["_ParseString"](el, attrs, ["url", "sizeList", "bodyCls", "bodyStyle", "footerCls", "footerStyle", "pagerCls", "pagerStyle", "onrowdblclick", "onrowclick", "onrowmousedown", "onrowcontextmenu", "oncellclick", "oncellmousedown", "oncellcontextmenu", "onbeforeload", "onpreload", "onloaderror", "onload", "ondrawcell", "oncellbeginedit", "onselectionchanged", "onshowrowdetail", "onhiderowdetail", "idField", "valueField", "ajaxMethod", "ondrawgroup", "pager", "oncellcommitedit", "oncellendedit", "headerContextMenu", "loadingMsg", "emptyText", "cellEditAction", "sortMode", "oncellvalidation"]);
        mini["_ParseBool"](el, attrs, ["showHeader", "showFooter", "showTop", "allowSortColumn", "allowMoveColumn", "allowResizeColumn", "showHGridLines", "showVGridLines", "showFilterRow", "showSummaryRow", "showFooter", "showTop", "fitColumns", "showLoading", "multiSelect", "allowAlternating", "resultAsData", "allowRowSelect", "enableHotTrack", "showPageIndex", "showPageSize", "showTotalCount", "checkSelectOnLoad", "allowResize", "autoLoad", "autoHideRowDetail", "allowCellSelect", "allowCellEdit", "allowCellWrap", "selectOnLoad", "virtualScroll", "collapseGroupOnLoad", "showGroupSummary", "showEmptyText", "allowCellValid"]);
        mini["_ParseInt"](el, attrs, ["columnWidth", "frozenStartColumn", "frozenEndColumn", "pageIndex", "pageSize"]);
        if (typeof attrs["sizeList"] == "string") attrs["sizeList"] = eval(attrs["sizeList"]);
        if (!attrs["idField"] && attrs["valueField"]) attrs["idField"] = attrs["valueField"];
        return attrs
    }
});
HbyG(mini.DataGrid, "datagrid");
mini_Column_Prototype = {
    Fmaf: function ($, _) {
        $ = this["getRow"] ? this["getRow"]($) : this["getNode"]($);
        _ = this["getColumn"](_);
        if (!$ || !_) return null;
        var A = this.YVUE($, _);
        return document.getElementById(A)
    },
    W6: function (A) {
        var $ = this.V4c ? this.V4c(A) : this._getNodeByEvent(A),
		_ = this.$LW(A);
        return {
            record: $,
            column: _
        }
    },
    $LW: function (B) {
        var _ = MRj9(B.target, this._cellCls);
        if (!_) _ = MRj9(B.target, this._headerCellCls);
        if (_) {
            var $ = _.id.split("$"),
			A = $[$.length - 1];
            return this.Dsr(A)
        }
        return null
    },
    Fkr: function ($) {
        return this.uid + "$column$" + $._id
    },
    getColumnBox: function (A) {
        var B = this.Fkr(A),
		_ = document.getElementById(B);
        if (_) {
            var $ = EcG(_);
            $.x -= 1;
            $.left = $.x;
            $.right = $.x + $.width;
            return $
        }
    },
    setColumns: function (value) {
        if (!mini.isArray(value)) value = [];
        this.columns = value;
        this.C1rM = {};
        this.NOq = {};
        this.HpsX = [];
        this.maxColumnLevel = 0;
        var level = 0;
        function init(column, index, parentColumn) {
            if (column.type) {
                if (!mini.isNull(column.header) && typeof column.header !== "function") if (column.header.trim() == "") delete column.header;
                var col = mini["_getColumn"](column.type);
                if (col) {
                    var _column = mini.copyTo({},
					column);
                    mini.copyTo(column, col);
                    mini.copyTo(column, _column)
                }
            }
            var width = parseInt(column.width);
            if (mini.isNumber(width) && String(width) == column.width) column.width = width + "px";
            if (mini.isNull(column.width)) column.width = this["columnWidth"] + "px";
            column.visible = column.visible !== false;
            column["allowResize"] = column.allowRresize !== false;
            column.allowMove = column.allowMove !== false;
            column.allowSort = column.allowSort === true;
            column.allowDrag = !!column.allowDrag;
            column["readOnly"] = !!column["readOnly"];
            if (!column._id) column._id = FrHN++;
            column._gridUID = this.uid;
            column["_rowIdField"] = this["_rowIdField"];
            column._pid = parentColumn == this ? -1 : parentColumn._id;
            this.C1rM[column._id] = column;
            if (column.name) this.NOq[column.name] = column;
            if (!column.columns || column.columns.length == 0) this.HpsX.push(column);
            column.level = level;
            level += 1;
            this["eachColumns"](column, init, this);
            level -= 1;
            if (column.level > this.maxColumnLevel) this.maxColumnLevel = column.level;
            if (typeof column.editor == "string") {
                var cls = mini.getClass(column.editor);
                if (cls) column.editor = {
                    type: column.editor
                };
                else column.editor = eval("(" + column.editor + ")")
            }
            if (typeof column.filter == "string") column.filter = eval("(" + column.filter + ")");
            if (column.filter && !column.filter.el) column.filter = mini.create(column.filter);
            if (typeof column.init == "function" && column.inited != true) column.init(this);
            column.inited = true
        }
        this["eachColumns"](this, init, this);
        if (this.GWF) this.GWF();
        if (this.Zm9F) this.Zm9F();
        this["doUpdate"]()
    },
    getColumns: function () {
        return this.columns
    },
    getBottomColumns: function () {
        return this.HpsX
    },
    getBottomVisibleColumns: function () {
        var A = [];
        for (var $ = 0, B = this.HpsX.length; $ < B; $++) {
            var _ = this.HpsX[$];
            if (this["isVisibleColumn"](_)) A.push(_)
        }
        return A
    },
    eachColumns: function (B, F, C) {
        var D = B.columns;
        if (D) {
            var _ = D.clone();
            for (var A = 0, E = _.length; A < E; A++) {
                var $ = _[A];
                if (F["call"](C, $, A, B) === false) break
            }
        }
    },
    getColumn: function ($) {
        var _ = typeof $;
        if (_ == "number") return this["getBottomColumns"]()[$];
        else if (_ == "object") return $;
        else return this.NOq[$]
    },
    Dsr: function ($) {
        return this.C1rM[$]
    },
    getParentColumn: function ($) {
        $ = this["getColumn"]($);
        var _ = $._pid;
        if (_ == -1) return this;
        return this.C1rM[_]
    },
    getAncestorColumns: function (A) {
        var _ = [];
        while (1) {
            var $ = this["getParentColumn"](A);
            if (!$ || $ == this) break;
            _[_.length] = $;
            A = $
        }
        _.reverse();
        return _
    },
    isAncestorColumn: function (_, B) {
        if (_ == B) return true;
        if (!_ || !B) return false;
        var A = this["getAncestorColumns"](B);
        for (var $ = 0, C = A.length; $ < C; $++) if (A[$] == _) return true;
        return false
    },
    isVisibleColumn: function (_) {
        _ = this["getColumn"](_);
        var A = this["getAncestorColumns"](_);
        for (var $ = 0, B = A.length; $ < B; $++) if (A[$].visible == false) return false;
        return true
    },
    updateColumn: function (_, $) {
        _ = this["getColumn"](_);
        if (!_) return;
        mini.copyTo(_, $);
        this["setColumns"](this.columns)
    },
    removeColumn: function ($) {
        $ = this["getColumn"]($);
        var _ = this["getParentColumn"]($);
        if ($ && _) {
            _.columns.remove($);
            this["setColumns"](this.columns)
        }
        return $
    },
    moveColumn: function (C, _, A) {
        C = this["getColumn"](C);
        _ = this["getColumn"](_);
        if (!C || !_ || !A || C == _) return;
        if (this["isAncestorColumn"](C, _)) return;
        var D = this["getParentColumn"](C);
        if (D) D.columns.remove(C);
        var B = _,
		$ = A;
        if ($ == "before") {
            B = this["getParentColumn"](_);
            $ = B.columns.indexOf(_)
        } else if ($ == "after") {
            B = this["getParentColumn"](_);
            $ = B.columns.indexOf(_) + 1
        } else if ($ == "add" || $ == "append") {
            if (!B.columns) B.columns = [];
            $ = B.columns.length
        } else if (!mini.isNumber($)) return;
        B.columns.insert($, C);
        this["setColumns"](this.columns)
    },
    hideColumn: function ($) {
        $ = this["getColumn"]($);
        if (!$) return;
        if (this["allowCellEdit"]) this.commitEdit();
        $.visible = false;
        this.V9s($, false);
        this.Uq$();
        this["doLayout"]();
        this.Ifk9()
    },
    showColumn: function ($) {
        $ = this["getColumn"]($);
        if (!$) return;
        if (this["allowCellEdit"]) this.commitEdit();
        $.visible = true;
        this.V9s($, true);
        this.Uq$();
        this["doLayout"]();
        this.Ifk9()
    },
    UA1: function () {
        var _ = this["getMaxColumnLevel"](),
		D = [];
        for (var C = 0, F = _; C <= F; C++) D.push([]);
        function A(C) {
            var D = mini["treeToArray"](C.columns, "columns"),
			A = 0;
            for (var $ = 0, B = D.length; $ < B; $++) {
                var _ = D[$];
                if (_.visible != true || _._hide == true) continue;
                if (!_.columns || _.columns.length == 0) A += 1
            }
            return A
        }
        var $ = mini["treeToArray"](this.columns, "columns");
        for (C = 0, F = $.length; C < F; C++) {
            var E = $[C],
			B = D[E.level];
            if (E.columns && E.columns.length > 0) E.colspan = A(E);
            if ((!E.columns || E.columns.length == 0) && E.level < _) E.rowspan = _ - E.level + 1;
            B.push(E)
        }
        return D
    },
    getMaxColumnLevel: function () {
        return this.maxColumnLevel
    }
};
mini.copyTo(mini.DataGrid.prototype, mini_Column_Prototype);
mini._GridSort = function ($) {
    this.grid = $;
    VNV($.VEE, "mousemove", this.__OnGridHeaderMouseMove, this);
    VNV($.VEE, "mouseout", this.__OnGridHeaderMouseOut, this)
};
mini._GridSort["prototype"] = {
    __OnGridHeaderMouseOut: function ($) {
        if (this.FNnWColumnEl) WeL(this.FNnWColumnEl, "mini-grid-headerCell-hover")
    },
    __OnGridHeaderMouseMove: function (_) {
        var $ = MRj9(_.target, "mini-grid-headerCell");
        if ($) {
            RE($, "mini-grid-headerCell-hover");
            this.FNnWColumnEl = $
        }
    },
    __onGridHeaderCellClick: function (B) {
        var $ = this.grid,
		A = MRj9(B.target, "mini-grid-headerCell");
        if (A) {
            var _ = $["getColumn"](A.id.split("$")[2]);
            if ($["allowMoveColumn"] && _ && _.allowDrag) {
                this.dragColumn = _;
                this._columnEl = A;
                this.getDrag().start(B)
            }
        }
    }
};
mini._ColumnSplitter = function ($) {
    this.grid = $;
    VNV(this.grid.el, "mousedown", this.Df4v, this);
    $.on("layout", this.Bu5, this)
};
mini._ColumnSplitter["prototype"] = {
    Bu5: function (A) {
        if (this.splittersEl) mini["removeNode"](this.splittersEl);
        if (this.splitterTimer) return;
        var $ = this.grid;
        if ($["isDisplay"]() == false) return;
        var _ = this;
        this.splitterTimer = setTimeout(function () {
            var H = $["getBottomColumns"](),
			I = H.length,
			E = EcG($.VEE, true),
			B = $.getScrollLeft(),
			G = [];
            for (var J = 0, F = H.length; J < F; J++) {
                var D = H[J],
				C = $["getColumnBox"](D);
                if (!C) break;
                var A = C.top - E.top,
				M = C.right - E.left - 2,
				K = C.height;
                if ($["isFrozen"] && $["isFrozen"]()) {
                    if (J >= $["frozenStartColumn"]);
                } else M += B;
                var N = $["getParentColumn"](D);
                if (N && N.columns) if (N.columns[N.columns.length - 1] == D) if (K + 5 < E.height) {
                    A = 0;
                    K = E.height
                }
                if ($["allowResizeColumn"] && D["allowResize"]) G[G.length] = "<div id=\"" + D._id + "\" class=\"mini-grid-splitter\" style=\"left:" + (M - 1) + "px;top:" + A + "px;height:" + K + "px;\"></div>"
            }
            var O = G.join("");
            _.splittersEl = document.createElement("div");
            _.splittersEl.className = "mini-grid-splitters";
            _.splittersEl.innerHTML = O;
            var L = $._getHeaderScrollEl();
            L.appendChild(_.splittersEl);
            _.splitterTimer = null
        },
		100)
    },
    Df4v: function (B) {
        var $ = this.grid,
		A = B.target;
        if (D4ge(A, "mini-grid-splitter")) {
            var _ = $.C1rM[A.id];
            if ($["allowResizeColumn"] && _ && _["allowResize"]) {
                this.splitterColumn = _;
                this.getDrag().start(B)
            }
        }
    },
    getDrag: function () {
        if (!this.drag) this.drag = new mini.Drag({
            capture: true,
            onStart: mini.createDelegate(this.J_sA, this),
            onMove: mini.createDelegate(this.KsZ, this),
            onStop: mini.createDelegate(this.ZYd, this)
        });
        return this.drag
    },
    J_sA: function (_) {
        var $ = this.grid,
		B = $["getColumnBox"](this.splitterColumn);
        this.columnBox = B;
        this.YL5C = mini.append(document.body, "<div class=\"mini-grid-proxy\"></div>");
        var A = $.getBox(true);
        A.x = B.x;
        A.width = B.width;
        A.right = B.right;
        Ggi(this.YL5C, A)
    },
    KsZ: function (A) {
        var $ = this.grid,
		B = mini.copyTo({},
		this.columnBox),
		_ = B.width + (A.now[0] - A.init[0]);
        if (_ < $.columnMinWidth) _ = $.columnMinWidth;
        if (_ > $.columnMaxWidth) _ = $.columnMaxWidth;
        SI9N(this.YL5C, _)
    },
    ZYd: function (E) {
        var $ = this.grid,
		F = EcG(this.YL5C),
		D = this,
		C = $["allowSortColumn"];
        $["allowSortColumn"] = false;
        setTimeout(function () {
            jQuery(D.YL5C).remove();
            D.YL5C = null;
            $["allowSortColumn"] = C
        },
		10);
        var G = this.splitterColumn,
		_ = parseInt(G.width);
        if (_ + "%" != G.width) {
            var A = $["getColumnWidth"](G),
			B = parseInt(_ / A * F.width);
            $["setColumnWidth"](G, B)
        }
    }
};
mini._ColumnMove = function ($) {
    this.grid = $;
    VNV(this.grid.el, "mousedown", this.Df4v, this)
};
mini._ColumnMove["prototype"] = {
    Df4v: function (B) {
        var $ = this.grid;
        if ($["isEditing"] && $["isEditing"]()) return;
        if (D4ge(B.target, "mini-grid-splitter")) return;
        if (B.button == mini.MouseButton.Right) return;
        var A = MRj9(B.target, $._headerCellCls);
        if (A) {
            var _ = $.$LW(B);
            if ($["allowMoveColumn"] && _ && _.allowMove) {
                this.dragColumn = _;
                this._columnEl = A;
                this.getDrag().start(B)
            }
        }
    },
    getDrag: function () {
        if (!this.drag) this.drag = new mini.Drag({
            capture: isIE9 ? false : true,
            onStart: mini.createDelegate(this.J_sA, this),
            onMove: mini.createDelegate(this.KsZ, this),
            onStop: mini.createDelegate(this.ZYd, this)
        });
        return this.drag
    },
    J_sA: function (_) {
        function A(_) {
            var A = _.header;
            if (typeof A == "function") A = A["call"]($, _);
            if (mini.isNull(A) || A === "") A = "&nbsp;";
            return A
        }
        var $ = this.grid;
        this.YL5C = mini.append(document.body, "<div class=\"mini-grid-columnproxy\"></div>");
        this.YL5C.innerHTML = "<div class=\"mini-grid-columnproxy-inner\" style=\"height:26px;\">" + A(this.dragColumn) + "</div>";
        mini["setXY"](this.YL5C, _.now[0] + 15, _.now[1] + 18);
        RE(this.YL5C, "mini-grid-no");
        this.moveTop = mini.append(document.body, "<div class=\"mini-grid-movetop\"></div>");
        this.moveBottom = mini.append(document.body, "<div class=\"mini-grid-movebottom\"></div>")
    },
    KsZ: function (A) {
        var $ = this.grid,
		G = A.now[0];
        mini["setXY"](this.YL5C, G + 15, A.now[1] + 18);
        this.targetColumn = this.insertAction = null;
        var D = MRj9(A.event.target, $._headerCellCls);
        if (D) {
            var C = $.$LW(A.event);
            if (C && C != this.dragColumn) {
                var _ = $["getParentColumn"](this.dragColumn),
				E = $["getParentColumn"](C);
                if (_ == E) {
                    this.targetColumn = C;
                    this.insertAction = "before";
                    var F = $["getColumnBox"](this.targetColumn);
                    if (G > F.x + F.width / 2) this.insertAction = "after"
                }
            }
        }
        if (this.targetColumn) {
            RE(this.YL5C, "mini-grid-ok");
            WeL(this.YL5C, "mini-grid-no");
            var B = $["getColumnBox"](this.targetColumn);
            this.moveTop.style.display = "block";
            this.moveBottom.style.display = "block";
            if (this.insertAction == "before") {
                mini["setXY"](this.moveTop, B.x - 4, B.y - 9);
                mini["setXY"](this.moveBottom, B.x - 4, B.bottom)
            } else {
                mini["setXY"](this.moveTop, B.right - 4, B.y - 9);
                mini["setXY"](this.moveBottom, B.right - 4, B.bottom)
            }
        } else {
            WeL(this.YL5C, "mini-grid-ok");
            RE(this.YL5C, "mini-grid-no");
            this.moveTop.style.display = "none";
            this.moveBottom.style.display = "none"
        }
    },
    ZYd: function (_) {
        var $ = this.grid;
        mini["removeNode"](this.YL5C);
        mini["removeNode"](this.moveTop);
        mini["removeNode"](this.moveBottom);
        $["moveColumn"](this.dragColumn, this.targetColumn, this.insertAction);
        this.YL5C = this.moveTop = this.moveBottom = this.dragColumn = this.targetColumn = null
    }
};
YrB = function ($) {
    this.grid = $;
    this.grid.on("cellmousedown", this.RKk, this);
    this.grid.on("cellclick", this.MhM, this);
    this.grid.on("celldblclick", this.MhM, this);
    VNV(this.grid.el, "keydown", this.DICN, this)
};
YrB["prototype"] = {
    DICN: function (G) {
        var $ = this.grid;
        if (Yma($.XdU, G.target) || Yma($.NfMd, G.target) || Yma($.Aca, G.target)) return;
        var A = $.getCurrentCell();
        if (G.shiftKey || G.ctrlKey) return;
        if (G.keyCode == 37 || G.keyCode == 38 || G.keyCode == 39 || G.keyCode == 40) G.preventDefault();
        var C = $["getBottomVisibleColumns"](),
		B = A ? A[1] : null,
		_ = A ? A[0] : null;
        if (!A) _ = $.getCurrent();
        var F = C.indexOf(B),
		D = $.indexOf(_),
		E = $.getData().length;
        switch (G.keyCode) {
            case 27:
                break;
            case 13:
                if ($["allowCellEdit"] && A) $["beginEditCell"]();
                break;
            case 37:
                if (B) {
                    if (F > 0) F -= 1
                } else F = 0;
                break;
            case 38:
                if (_) {
                    if (D > 0) D -= 1
                } else D = 0;
                if (D != 0 && $.isVirtualScroll()) if ($._viewRegion.start > D) {
                    $.$kJ.scrollTop -= $._rowHeight;
                    $._tryUpdateScroll()
                }
                break;
            case 39:
                if (B) {
                    if (F < C.length - 1) F += 1
                } else F = 0;
                break;
            case 40:
                if (_) {
                    if (D < E - 1) D += 1
                } else D = 0;
                if ($.isVirtualScroll()) if ($._viewRegion.end < D) {
                    $.$kJ.scrollTop += $._rowHeight;
                    $._tryUpdateScroll()
                }
                break;
            default:
                break
        }
        B = C[F];
        _ = $.getAt(D);
        if (B && _ && $["allowCellSelect"]) {
            A = [_, B];
            $["setCurrentCell"](A)
        }
        if (_ && $["allowRowSelect"]) {
            $["deselectAll"]();
            $["setCurrent"](_)
        }
    },
    MhM: function (A) {
        if (this.grid.cellEditAction != A.type) return;
        var $ = A.record,
		_ = A.column;
        if (!_["readOnly"] && !this.grid["isReadOnly"]()) if (A.htmlEvent.shiftKey || A.htmlEvent.ctrlKey);
        else this.grid["beginEditCell"]()
    },
    RKk: function (C) {
        var _ = C.record,
		B = C.column,
		$ = this.grid;
        if (this.grid["allowCellSelect"]) {
            var A = [_, B];
            this.grid["setCurrentCell"](A)
        }
        if ($["allowRowSelect"]) if ($["multiSelect"]) {
            this.grid.el.onselectstart = function () { };
            if (C.htmlEvent.shiftKey) {
                this.grid.el.onselectstart = function () {
                    return false
                };
                C.htmlEvent.preventDefault();
                if (!this.currentRecord) {
                    this.grid["select"](_);
                    this.currentRecord = this.grid["getSelected"]()
                } else {
                    this.grid["deselectAll"]();
                    this.grid.selectRange(this.currentRecord, _)
                }
            } else {
                this.grid.el.onselectstart = function () { };
                if (C.htmlEvent.ctrlKey) {
                    this.grid.el.onselectstart = function () {
                        return false
                    };
                    C.htmlEvent.preventDefault()
                }
                if (C.column._multiRowSelect === true || C.htmlEvent.ctrlKey) {
                    if ($["isSelected"](_)) $["deselect"](_);
                    else $["select"](_)
                } else if ($["isSelected"](_));
                else {
                    $["deselectAll"]();
                    $["select"](_)
                }
                this.currentRecord = this.grid["getSelected"]()
            }
        } else if (!$["isSelected"](_)) {
            $["deselectAll"]();
            $["select"](_)
        } else if (C.htmlEvent.ctrlKey) $["deselectAll"]()
    }
};
mini._CellToolTip = function ($) {
    this.grid = $;
    VNV(this.grid.el, "mousemove", this.__onGridMouseMove, this)
};
mini._CellToolTip["prototype"] = {
    __onGridMouseMove: function (D) {
        var $ = this.grid,
		A = $.W6(D),
		_ = $.Fmaf(A.record, A.column),
		B = $.getCellError(A.record, A.column);
        if (_) {
            if (B) {
                _.title = B.errorText;
                return
            }
            if (_.firstChild) if (D4ge(_.firstChild, "mini-grid-cell-inner") || D4ge(_.firstChild, "mini-treegrid-treecolumn-inner")) _ = _.firstChild;
            if (_.scrollWidth > _.clientWidth) {
                var C = _.innerText || _.textContent || "";
                _.title = C.trim()
            } else _.title = ""
        }
    }
};
mini_CellValidator_Prototype = {
    getCellErrors: function () {
        return this._cellErrors
    },
    getCellError: function ($, _) {
        $ = this["getNode"] ? this["getNode"]($) : this["getRow"]($);
        _ = this["getColumn"](_);
        if (!$ || !_) return;
        var A = $[this._rowIdField] + "$" + _._id;
        return this._cellMapErrors[A]
    },
    isValid: function () {
        return this._cellErrors.length == 0
    },
    validate: function () {
        var A = this.data;
        for (var $ = 0, B = A.length; $ < B; $++) {
            var _ = A[$];
            this.validateRow(_)
        }
    },
    validateRow: function (_) {
        var B = this["getBottomColumns"]();
        for (var $ = 0, C = B.length; $ < C; $++) {
            var A = B[$];
            this.validateCell(_, A)
        }
    },
    validateCell: function ($, _) {
        $ = this["getNode"] ? this["getNode"]($) : this["getRow"]($);
        _ = this["getColumn"](_);
        if (!$ || !_) return;
        var A = {
            record: $,
            row: $,
            node: $,
            column: _,
            field: _.field,
            value: $[_.field],
            isValid: true,
            errorText: ""
        };
        if (_.vtype) mini._ValidateVType(_.vtype, A.value, A, _);
        this.fire("cellvalidation", A);
        this.setCellIsValid($, _, A.isValid, A.errorText)
    },
    setIsValid: function (_) {
        if (_) {
            var A = this._cellErrors.clone();
            for (var $ = 0, B = A.length; $ < B; $++) {
                var C = A[$];
                this.setCellIsValid(C.record, C.column, true)
            }
        }
    },
    setCellIsValid: function (_, A, B, D) {
        _ = this["getNode"] ? this["getNode"](_) : this["getRow"](_);
        A = this["getColumn"](A);
        if (!_ || !A) return;
        var E = _[this._rowIdField] + "$" + A._id,
		$ = this.Fmaf(_, A),
		C = this._cellMapErrors[E];
        delete this._cellMapErrors[E];
        this._cellErrors.remove(C);
        if (B === true) {
            if ($ && C) WeL($, "mini-grid-cell-error")
        } else {
            C = {
                record: _,
                column: A,
                isValid: B,
                errorText: D
            };
            this._cellMapErrors[E] = C;
            this._cellErrors.add(C);
            if ($) RE($, "mini-grid-cell-error")
        }
    }
};
mini.copyTo(mini.DataGrid.prototype, mini_CellValidator_Prototype);
mini.GridEditor = function () {
    this._inited = true;
    mini.Control["superclass"]["constructor"]["call"](this);
    this["_create"]();
    this.el.uid = this.uid;
    this["_initEvents"]();
    this.Yi();
    this["addCls"](this.uiCls)
};
Pv_r(mini.GridEditor, mini.Control, {
    el: null,
    _create: function () {
        this.el = document.createElement("input");
        this.el.type = "text";
        this.el.style.width = "100%"
    },
    getValue: function () {
        return this.el.value
    },
    setValue: function ($) {
        this.el.value = $
    },
    setWidth: function ($) { }
});
mini.Pager = function () {
    mini.Pager["superclass"]["constructor"]["call"](this)
};
Pv_r(mini.Pager, mini.Control, {
    pageIndex: 0,
    pageSize: 10,
    totalCount: 0,
    totalPage: 0,
    showPageIndex: true,
    showPageSize: true,
    showTotalCount: true,
    showPageInfo: true,
    _clearBorder: false,
    showButtonText: false,
    showButtonIcon: true,
    firstText: "\u9996\u9875",
    prevText: "\u4e0a\u4e00\u9875",
    nextText: "\u4e0b\u4e00\u9875",
    lastText: "\u5c3e\u9875",
    pageInfoText: "\u6bcf\u9875 {0} \u6761,\u5171 {1} \u6761",
    sizeList: [10, 20, 50, 100],
    uiCls: "mini-pager",
    _create: function () {
        this.el = document.createElement("div");
        this.el.className = "mini-pager";
        var $ = "<div class=\"mini-pager-left\"></div><div class=\"mini-pager-right\"></div>";
        this.el.innerHTML = $;
        this.buttonsEl = this._leftEl = this.el.childNodes[0];
        this._rightEl = this.el.childNodes[1];
        this.sizeEl = mini.append(this.buttonsEl, "<span class=\"mini-pager-size\"></span>");
        this.sizeCombo = new mini.ComboBox();
        this.sizeCombo.setName("pagesize");
        this.sizeCombo["setWidth"](45);
        this.sizeCombo["render"](this.sizeEl);
        mini.append(this.sizeEl, "<span class=\"separator\"></span>");
        this.firstButton = new mini.Button();
        this.firstButton["render"](this.buttonsEl);
        this.prevButton = new mini.Button();
        this.prevButton["render"](this.buttonsEl);
        this.indexEl = document.createElement("span");
        this.indexEl.className = "mini-pager-index";
        this.indexEl.innerHTML = "<input id=\"\" type=\"text\" class=\"mini-pager-num\"/><span class=\"mini-pager-pages\">/ 0</span>";
        this.buttonsEl.appendChild(this.indexEl);
        this.numInput = this.indexEl.firstChild;
        this.pagesLabel = this.indexEl.lastChild;
        this.nextButton = new mini.Button();
        this.nextButton["render"](this.buttonsEl);
        this.lastButton = new mini.Button();
        this.lastButton["render"](this.buttonsEl);
        this.firstButton.setPlain(true);
        this.prevButton.setPlain(true);
        this.nextButton.setPlain(true);
        this.lastButton.setPlain(true);
        this.update()
    },
    destroy: function ($) {
        if (this.pageSelect) {
            mini["clearEvent"](this.pageSelect);
            this.pageSelect = null
        }
        if (this.numInput) {
            mini["clearEvent"](this.numInput);
            this.numInput = null
        }
        this.sizeEl = null;
        this.buttonsEl = null;
        mini.Pager["superclass"]["destroy"]["call"](this, $)
    },
    _initEvents: function () {
        mini.Pager["superclass"]["_initEvents"]["call"](this);
        this.firstButton.on("click",
		function ($) {
		    this.CSa(0)
		},
		this);
        this.prevButton.on("click",
		function ($) {
		    this.CSa(this["pageIndex"] - 1)
		},
		this);
        this.nextButton.on("click",
		function ($) {
		    this.CSa(this["pageIndex"] + 1)
		},
		this);
        this.lastButton.on("click",
		function ($) {
		    this.CSa(this.totalPage)
		},
		this);
        function $() {
            var $ = parseInt(this.numInput.value);
            if (isNaN($)) this.update();
            else this.CSa($ - 1)
        }
        VNV(this.numInput, "change",
		function (_) {
		    $["call"](this)
		},
		this);
        VNV(this.numInput, "keydown",
		function (_) {
		    if (_.keyCode == 13) {
		        $["call"](this);
		        _.stopPropagation()
		    }
		},
		this);
        this.sizeCombo.on("valuechanged", this.Ui, this)
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        mini.layout(this._leftEl);
        mini.layout(this._rightEl)
    },
    setPageIndex: function ($) {
        if (isNaN($)) return;
        this["pageIndex"] = $;
        this.update()
    },
    getPageIndex: function () {
        return this["pageIndex"]
    },
    setPageSize: function ($) {
        if (isNaN($)) return;
        this["pageSize"] = $;
        this.update()
    },
    getPageSize: function () {
        return this["pageSize"]
    },
    setTotalCount: function ($) {
        $ = parseInt($);
        if (isNaN($)) return;
        this["totalCount"] = $;
        this.update()
    },
    getTotalCount: function () {
        return this["totalCount"]
    },
    setSizeList: function ($) {
        if (!mini.isArray($)) return;
        this["sizeList"] = $;
        this.update()
    },
    getSizeList: function () {
        return this["sizeList"]
    },
    setShowPageSize: function ($) {
        this.showPageSize = $;
        this.update()
    },
    getShowPageSize: function () {
        return this.showPageSize
    },
    setShowPageIndex: function ($) {
        this.showPageIndex = $;
        this.update()
    },
    getShowPageIndex: function () {
        return this.showPageIndex
    },
    setShowTotalCount: function ($) {
        this.showTotalCount = $;
        this.update()
    },
    getShowTotalCount: function () {
        return this.showTotalCount
    },
    setShowPageInfo: function ($) {
        this.showPageInfo = $;
        this.update()
    },
    getShowPageInfo: function () {
        return this.showPageInfo
    },
    getTotalPage: function () {
        return this.totalPage
    },
    update: function ($, H, F) {
        if (mini.isNumber($)) this["pageIndex"] = parseInt($);
        if (mini.isNumber(H)) this["pageSize"] = parseInt(H);
        if (mini.isNumber(F)) this["totalCount"] = parseInt(F);
        this.totalPage = parseInt(this["totalCount"] / this["pageSize"]) + 1;
        if ((this.totalPage - 1) * this["pageSize"] == this["totalCount"]) this.totalPage -= 1;
        if (this["totalCount"] == 0) this.totalPage = 0;
        if (this["pageIndex"] > this.totalPage - 1) this["pageIndex"] = this.totalPage - 1;
        if (this["pageIndex"] <= 0) this["pageIndex"] = 0;
        if (this.totalPage <= 0) this.totalPage = 0;
        this.firstButton.enable();
        this.prevButton.enable();
        this.nextButton.enable();
        this.lastButton.enable();
        if (this["pageIndex"] == 0) {
            this.firstButton.disable();
            this.prevButton.disable()
        }
        if (this["pageIndex"] >= this.totalPage - 1) {
            this.nextButton.disable();
            this.lastButton.disable()
        }
        this.numInput.value = this["pageIndex"] > -1 ? this["pageIndex"] + 1 : 0;
        this.pagesLabel.innerHTML = "/ " + this.totalPage;
        var K = this["sizeList"].clone();
        if (K.indexOf(this["pageSize"]) == -1) {
            K.push(this["pageSize"]);
            K = K.sort(function ($, _) {
                return $ > _
            })
        }
        var _ = [];
        for (var E = 0, B = K.length; E < B; E++) {
            var D = K[E],
			G = {};
            G.text = D;
            G.id = D;
            _.push(G)
        }
        this.sizeCombo["setData"](_);
        this.sizeCombo["setValue"](this["pageSize"]);
        var A = this.firstText,
		J = this.prevText,
		C = this.nextText,
		I = this.lastText;
        if (this.showButtonText == false) A = J = C = I = "";
        this.firstButton["setText"](A);
        this.prevButton["setText"](J);
        this.nextButton["setText"](C);
        this.lastButton["setText"](I);
        A = this.firstText,
		J = this.prevText,
		C = this.nextText,
		I = this.lastText;
        if (this.showButtonText == true) A = J = C = I = "";
        this.firstButton.setTooltip(A);
        this.prevButton.setTooltip(J);
        this.nextButton.setTooltip(C);
        this.lastButton.setTooltip(I);
        this.firstButton.setIconCls(this.showButtonIcon ? "mini-pager-first" : "");
        this.prevButton.setIconCls(this.showButtonIcon ? "mini-pager-prev" : "");
        this.nextButton.setIconCls(this.showButtonIcon ? "mini-pager-next" : "");
        this.lastButton.setIconCls(this.showButtonIcon ? "mini-pager-last" : "");
        this._rightEl.innerHTML = String.format(this.pageInfoText, this.pageSize, this["totalCount"]);
        this.indexEl.style.display = this.showPageIndex ? "" : "none";
        this.sizeEl.style.display = this.showPageSize ? "" : "none";
        this._rightEl.style.display = this.showPageInfo ? "" : "none"
    },
    Ui: function (_) {
        var $ = parseInt(this.sizeCombo.getValue());
        this.CSa(0, $)
    },
    CSa: function ($, _) {
        var A = {
            pageIndex: mini.isNumber($) ? $ : this.pageIndex,
            pageSize: mini.isNumber(_) ? _ : this.pageSize,
            cancel: false
        };
        if (A["pageIndex"] > this.totalPage - 1) A["pageIndex"] = this.totalPage - 1;
        if (A["pageIndex"] < 0) A["pageIndex"] = 0;
        this.fire("pagechanged", A);
        if (A.cancel == false) this.update(A.pageIndex, A["pageSize"])
    },
    onPageChanged: function (_, $) {
        this.on("pagechanged", _, $)
    },
    getAttrs: function (el) {
        var attrs = mini.Pager["superclass"]["getAttrs"]["call"](this, el);
        mini["_ParseString"](el, attrs, ["onpagechanged", "sizeList"]);
        mini["_ParseBool"](el, attrs, ["showPageIndex", "showPageSize", "showTotalCount", "showPageInfo"]);
        mini["_ParseInt"](el, attrs, ["pageIndex", "pageSize", "totalCount"]);
        if (typeof attrs["sizeList"] == "string") attrs["sizeList"] = eval(attrs["sizeList"]);
        return attrs
    }
});
HbyG(mini.Pager, "pager");
mini.TreeGrid = function () {
    this.columns = [];
    this.HpsX = [];
    this.C1rM = {};
    this.NOq = {};
    this._cellErrors = [];
    this._cellMapErrors = {};
    mini.TreeGrid["superclass"]["constructor"]["call"](this);
    this.Xj5.style.display = this["allowResize"] ? "" : "none"
};
Pv_r(mini.TreeGrid, mini.Tree, {
    _rowIdField: "_id",
    width: 300,
    height: 180,
    allowResize: false,
    treeColumn: "",
    columns: [],
    columnWidth: 80,
    allowResizeColumn: true,
    allowMoveColumn: true,
    UkA: true,
    _headerCellCls: "mini-treegrid-headerCell",
    _cellCls: "mini-treegrid-cell",
    ZeR: "mini-treegrid-border",
    X34: "mini-treegrid-header",
    E5e: "mini-treegrid-body",
    ME$h: "mini-treegrid-node",
    Xu: "mini-treegrid-nodes",
    U4bQ: "mini-treegrid-selectedNode",
    UdPE: "mini-treegrid-hoverNode",
    IpN: "mini-treegrid-expand",
    CA6: "mini-treegrid-collapse",
    SR: "mini-treegrid-ec-icon",
    WnSi: "mini-treegrid-nodeTitle",
    EYQR: function (_) {
        if (!_) return null;
        var $ = this.Ro(_);
        return $
    },
    uiCls: "mini-treegrid",
    _create: function () {
        mini.TreeGrid["superclass"]["_create"]["call"](this);
        this.Xj5 = mini.append(this._firstChild, "<div class=\"mini-grid-resizeGrid\" style=\"\"></div>");
        VNV(this.$kJ, "scroll", this.AXlX, this);
        this.TJoj = new BJ5(this);
        this._ColumnMove = new mini._ColumnMove(this);
        this.Brk = new mini._ColumnSplitter(this);
        this._CellTip = new mini._CellToolTip(this)
    },
    Fkr: function ($) {
        return this.uid + "$column$" + $.id
    },
    _getHeaderScrollEl: function () {
        return this.VEE.firstChild
    },
    PSR: function (D) {
        var F = "",
		B = this["getBottomColumns"]();
        if (isIE) {
            if (isIE6 || isIE7 || (isIE8 && !jQuery.boxModel) || (isIE9 && !jQuery.boxModel)) F += "<tr style=\"display:none;\">";
            else F += "<tr >"
        } else F += "<tr>";
        for (var $ = 0, C = B.length; $ < C; $++) {
            var A = B[$],
			_ = A.width,
			E = this.Fkr(A) + "$" + D;
            F += "<td id=\"" + E + "\" style=\"padding:0;border:0;margin:0;height:0;";
            if (A.width) F += "width:" + A.width;
            F += "\" ></td>"
        }
        F += "</tr>";
        return F
    },
    Uq$: function () {
        var E = this["getBottomColumns"](),
		F = [];
        F[F.length] = "<div class=\"mini-treegrid-headerInner\"><table class=\"mini-treegrid-table\" cellspacing=\"0\" cellpadding=\"0\">";
        F[F.length] = this.PSR();
        F[F.length] = "<tr>";
        for (var D = 0, _ = E.length; D < _; D++) {
            var B = E[D],
			C = B.header;
            if (typeof C == "function") C = C["call"](this, B);
            if (mini.isNull(C) || C === "") C = "&nbsp;";
            var A = B.width;
            if (mini.isNumber(A)) A = A + "px";
            var $ = this.Fkr(B);
            F[F.length] = "<td id=\"";
            F[F.length] = $;
            F[F.length] = "\" class=\"mini-treegrid-headerCell ";
            if (B.headerCls) F[F.length] = B.headerCls;
            F[F.length] = "\" style=\"";
            if (B.headerStyle) F[F.length] = B.headerStyle + ";";
            if (A) F[F.length] = "width:" + A + ";";
            if (B.headerAlign) F[F.length] = "text-align:" + B.headerAlign + ";";
            F[F.length] = "\">";
            F[F.length] = C;
            F[F.length] = "</td>"
        }
        F[F.length] = "</tr></table></div>";
        this.VEE.innerHTML = F.join("")
    },
    XS9: function (B, M, G) {
        var K = !G;
        if (!G) G = [];
        var H = B[this.textField];
        if (H === null || H === undefined) H = "";
        var I = this.isLeaf(B),
		$ = this.getLevel(B),
		D = "";
        if (!I) D = this.isExpandedNode(B) ? this.IpN : this.CA6;
        if (this.PI3 == B) D += " " + this.U4bQ;
        var E = this["getBottomColumns"]();
        G[G.length] = "<table class=\"mini-treegrid-nodeTitle ";
        G[G.length] = D;
        G[G.length] = "\" cellspacing=\"0\" cellpadding=\"0\">";
        G[G.length] = this.PSR();
        G[G.length] = "<tr>";
        for (var J = 0, _ = E.length; J < _; J++) {
            var C = E[J],
			F = this.YVUE(B, C),
			L = this.Y2v(B, C),
			A = C.width;
            if (typeof A == "number") A = A + "px";
            G[G.length] = "<td id=\"";
            G[G.length] = F;
            G[G.length] = "\" class=\"mini-treegrid-cell ";
            if (L.cellCls) G[G.length] = L.cellCls;
            G[G.length] = "\" style=\"";
            if (L.cellStyle) {
                G[G.length] = L.cellStyle;
                G[G.length] = ";"
            }
            if (C.align) {
                G[G.length] = "text-align:";
                G[G.length] = C.align;
                G[G.length] = ";"
            }
            G[G.length] = "\">";
            G[G.length] = L.cellHtml;
            G[G.length] = "</td>";
            if (L.rowCls) rowCls = L.rowCls;
            if (L.rowStyle) rowStyle = L.rowStyle
        }
        G[G.length] = "</table>";
        if (K) return G.join("")
    },
    doUpdate: function () {
        if (!this.N2b) return;
        this.Uq$();
        var $ = new Date(),
		_ = this._getViewChildNodes(this.root),
		B = [];
        this.UhvM(_, this.root, B);
        var A = B.join("");
        this.$kJ.innerHTML = A;
        this.Mso()
    },
    getScrollLeft: function () {
        return this.$kJ.scrollLeft
    },
    doLayout: function () {
        if (!this.canLayout()) return;
        var C = this["isAutoHeight"](),
		D = this["isAutoWidth"](),
		_ = this["getWidth"](true),
		A = this["getHeight"](true),
		B = this["getHeaderHeight"](),
		$ = A - B;
        this.$kJ.style.width = _ + "px";
        this.$kJ.style.height = $ + "px";
        this.VYt();
        this.fire("layout")
    },
    VYt: function () {
        var B = this.$kJ.scrollHeight,
		E = this.$kJ.clientHeight,
		A = this["getWidth"](true);
        if (isIE) {
            var _ = this.VEE.firstChild.firstChild,
			D = this.$kJ.firstChild;
            if (E >= B) {
                if (D) D.style.width = "100%";
                if (_) _.style.width = "100%"
            } else {
                if (D) {
                    var $ = parseInt(D.parentNode.offsetWidth - 17) + "px";
                    D.style.width = $
                }
                if (_) _.style.width = $
            }
        }
        if (E < B) this.VEE.firstChild.style.width = (A - 17) + "px";
        else this.VEE.firstChild.style.width = "100%";
        try {
            $ = this.VEE.firstChild.firstChild.offsetWidth;
            this.$kJ.firstChild.style.width = $ + "px"
        } catch (C) { }
        this.AXlX()
    },
    getHeaderHeight: function () {
        return Nf(this.VEE)
    },
    Y2v: function ($, B) {
        var D = this["showCheckBox"];
        if (D && this.hasChildren($)) D = this["showFolderCheckBox"];
        var _ = $[B.field],
		C = {
		    isLeaf: this.isLeaf($),
		    rowIndex: this.indexOf($),
		    showCheckBox: D,
		    iconCls: this.getNodeIcon($),
		    showTreeIcon: this.showTreeIcon,
		    sender: this,
		    record: $,
		    row: $,
		    node: $,
		    column: B,
		    field: B ? B.field : null,
		    value: _,
		    cellHtml: _,
		    rowCls: null,
		    cellCls: B ? (B.cellCls || "") : "",
		    rowStyle: null,
		    cellStyle: B ? (B.cellStyle || "") : ""
		};
        if (B.dateFormat) if (mini.isDate(C.value)) C.cellHtml = mini.formatDate(_, B.dateFormat);
        else C.cellHtml = _;
        var A = B.renderer;
        if (A) {
            fn = typeof A == "function" ? A : window[A];
            if (fn) C.cellHtml = fn["call"](B, C)
        }
        this.fire("drawcell", C);
        if (C.cellHtml === null || C.cellHtml === undefined || C.cellHtml === "") C.cellHtml = "&nbsp;";
        if (!this.treeColumn || this.treeColumn !== B.name) return C;
        this.PBP(C);
        return C
    },
    PBP: function (H) {
        var A = H.node;
        if (mini.isNull(H["showTreeIcon"])) H["showTreeIcon"] = this["showTreeIcon"];
        var G = H.cellHtml,
		B = this.isLeaf(A),
		$ = this.getLevel(A) * 18,
		D = "";
        if (H.cellCls) H.cellCls += " mini-treegrid-treecolumn ";
        else H.cellCls = " mini-treegrid-treecolumn ";
        var F = "<div class=\"mini-treegrid-treecolumn-inner " + D + "\">";
        if (!B) F += "<a href=\"#\" onclick=\"return false;\"  hidefocus class=\"" + this.SR + "\" style=\"left:" + ($) + "px;\"></a>";
        $ += 18;
        if (H["showTreeIcon"]) {
            var _ = this.getNodeIcon(A);
            F += "<div class=\"" + _ + " mini-treegrid-nodeicon\" style=\"left:" + $ + "px;\"></div>";
            $ += 18
        }
        G = "<span class=\"mini-tree-nodetext\">" + G + "</span>";
        if (H["showCheckBox"]) {
            var E = this.$n1A(A),
			C = this.isCheckedNode(A);
            G = "<input type=\"checkbox\" id=\"" + E + "\" class=\"" + this.$Sn + "\" hidefocus " + (C ? "checked" : "") + "/>" + G
        }
        F += "<div class=\"mini-treegrid-nodeshow\" style=\"margin-left:" + ($ + 2) + "px;\">" + G + "</div>";
        F += "</div>";
        G = F;
        H.cellHtml = G
    },
    setTreeColumn: function ($) {
        if (this.treeColumn != $) {
            this.treeColumn = $;
            this["doUpdate"]()
        }
    },
    getTreeColumn: function ($) {
        return this.treeColumn
    },
    setAllowResizeColumn: function ($) {
        this["allowResizeColumn"] = $
    },
    getAllowResizeColumn: function ($) {
        return this["allowResizeColumn"]
    },
    setAllowMoveColumn: function ($) {
        this["allowMoveColumn"] = $
    },
    getAllowMoveColumn: function ($) {
        return this["allowMoveColumn"]
    },
    setAllowResize: function ($) {
        this["allowResize"] = $;
        this.Xj5.style.display = this["allowResize"] ? "" : "none"
    },
    getAllowResize: function () {
        return this["allowResize"]
    },
    YVUE: function (_, $) {
        return this.uid + "$" + _._id + "$" + $._id
    },
    setColumnWidth: function (_, $) {
        _ = this["getColumn"](_);
        if (!_) return;
        if (mini.isNumber($)) $ += "px";
        _.width = $;
        this["doUpdate"]()
    },
    getColumnWidth: function (_) {
        var $ = this["getColumnBox"](_);
        return $ ? $.width : 0
    },
    AXlX: function (_) {
        var $ = this.$kJ.scrollLeft;
        this.VEE.firstChild.scrollLeft = $
    },
    getAttrs: function (_) {
        var E = mini.TreeGrid["superclass"]["getAttrs"]["call"](this, _);
        mini["_ParseString"](_, E, ["treeColumn", "ondrawcell"]);
        mini["_ParseBool"](_, E, ["allowResizeColumn", "allowMoveColumn", "allowResize"]);
        var C = mini["getChildNodes"](_);
        for (var $ = 0, D = C.length; $ < D; $++) {
            var B = C[$],
			A = jQuery(B).attr("property");
            if (!A) continue;
            A = A.toLowerCase();
            if (A == "columns") E.columns = mini._ParseColumns(B)
        }
        delete E.data;
        return E
    }
});
mini.copyTo(mini.TreeGrid.prototype, mini_Column_Prototype);
mini.copyTo(mini.TreeGrid.prototype, mini_CellValidator_Prototype);
HbyG(mini.TreeGrid, "treegrid");
//mini.RadioButtonList = Bm,
//mini.ValidatorBase = GgA,
//mini.AutoComplete = $N2,
//mini.CheckBoxList = WZs,
//mini.DataBinding = $O99,
//mini.OutlookTree = Ayq,
//mini.OutlookMenu = TsK,
//mini.TextBoxList = Ekk,
//mini.TimeSpinner = Y2qH,
//mini.ListControl = Bb,
//mini.OutlookBar = CON,
//mini.FileUpload = El$a,
//mini.TreeSelect = UfAz,
//mini.DatePicker = SvH,
//mini.ButtonEdit = WA,
//mini.PopupEdit = SwAA,
//mini.Component = RcR,
//mini.TreeGrid = Xr,
//mini.DataGrid = Zfs,
//mini.MenuItem = NG,
//mini.Splitter = XN7,
//mini.HtmlFile = QAH,
//mini.Calendar = $vd,
//mini.ComboBox = I5W,
//mini.TextArea = _B,
//mini.Password = VLs,
//mini.CheckBox = QbIw,
//mini.DataSet = $K4,
//mini.Include = ZE$,
//mini.Spinner = V1,
//mini.ListBox = YPy,
//mini.TextBox = RPK,
//mini.Control = Wy$,
//mini.Layout = D2cw,
//mini.Window = NH,
//mini.Lookup = RaqY,
//mini.Button = WlI,
//mini.Hidden = FbT,
//mini.Pager = Y_k,
//mini.Panel = BAwz,
//mini.Popup = SEV5,
//mini.Tree = IN,
//mini.Menu = OEC,
//mini.Tabs = JF,
//mini.Fit = SXB,
//mini.Box = FLV;
mini.locale = "en-US";
mini.dateInfo = {
    monthsLong: ["\u4e00\u6708", "\u4e8c\u6708", "\u4e09\u6708", "\u56db\u6708", "\u4e94\u6708", "\u516d\u6708", "\u4e03\u6708", "\u516b\u6708", "\u4e5d\u6708", "\u5341\u6708", "\u5341\u4e00\u6708", "\u5341\u4e8c\u6708"],
    monthsShort: ["1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708"],
    daysLong: ["\u661f\u671f\u65e5", "\u661f\u671f\u4e00", "\u661f\u671f\u4e8c", "\u661f\u671f\u4e09", "\u661f\u671f\u56db", "\u661f\u671f\u4e94", "\u661f\u671f\u516d"],
    daysShort: ["\u65e5", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d"],
    quarterLong: ["\u4e00\u5b63\u5ea6", "\u4e8c\u5b63\u5ea6", "\u4e09\u5b63\u5ea6", "\u56db\u5b63\u5ea6"],
    quarterShort: ["Q1", "Q2", "Q2", "Q4"],
    halfYearLong: ["\u4e0a\u534a\u5e74", "\u4e0b\u534a\u5e74"],
    patterns: {
        "d": "yyyy-M-d",
        "D": "yyyy\u5e74M\u6708d\u65e5",
        "f": "yyyy\u5e74M\u6708d\u65e5 H:mm",
        "F": "yyyy\u5e74M\u6708d\u65e5 H:mm:ss",
        "g": "yyyy-M-d H:mm",
        "G": "yyyy-M-d H:mm:ss",
        "m": "MMMd\u65e5",
        "o": "yyyy-MM-ddTHH:mm:ss.fff",
        "s": "yyyy-MM-ddTHH:mm:ss",
        "t": "H:mm",
        "T": "H:mm:ss",
        "U": "yyyy\u5e74M\u6708d\u65e5 HH:mm:ss",
        "y": "yyyy\u5e74MM\u6708"
    },
    tt: {
        "AM": "\u4e0a\u5348",
        "PM": "\u4e0b\u5348"
    },
    ten: {
        "Early": "\u4e0a\u65ec",
        "Mid": "\u4e2d\u65ec",
        "Late": "\u4e0b\u65ec"
    },
    today: "\u4eca\u5929",
    clockType: 24
};
if (mini.Calendar) mini.copyTo(mini.Calendar.prototype, {
    firstDayOfWeek: 0,
    todayText: "\u4eca\u5929",
    clearText: "\u6e05\u9664",
    okText: "\u786e\u5b9a",
    cancelText: "\u53d6\u6d88",
    daysShort: ["\u65e5", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d"],
    format: "yyyy\u5e74MM\u6708",
    timeFormat: "H:mm"
});
for (var id in mini) {
    var clazz = mini[id];
    if (clazz && clazz["prototype"] && clazz["prototype"].isControl) clazz["prototype"]["requiredErrorText"] = "\u4e0d\u80fd\u4e3a\u7a7a"
}
if (mini.VTypes) mini.copyTo(mini.VTypes, {
    requiredErrorText: "\u4e0d\u80fd\u4e3a\u7a7a",
    emailErrorText: "\u8bf7\u8f93\u5165\u90ae\u4ef6\u683c\u5f0f",
    urlErrorText: "\u8bf7\u8f93\u5165URL\u683c\u5f0f",
    floatErrorText: "\u8bf7\u8f93\u5165\u6570\u5b57",
    intErrorText: "\u8bf7\u8f93\u5165\u6574\u6570",
    dateErrorText: "\u8bf7\u8f93\u5165\u65e5\u671f\u683c\u5f0f {0}",
    maxLengthErrorText: "\u4e0d\u80fd\u8d85\u8fc7 {0} \u4e2a\u5b57\u7b26",
    minLengthErrorText: "\u4e0d\u80fd\u5c11\u4e8e {0} \u4e2a\u5b57\u7b26",
    maxErrorText: "\u6570\u5b57\u4e0d\u80fd\u5927\u4e8e {0} ",
    minErrorText: "\u6570\u5b57\u4e0d\u80fd\u5c0f\u4e8e {0} ",
    rangeLengthErrorText: "\u5b57\u7b26\u957f\u5ea6\u5fc5\u987b\u5728 {0} \u5230 {1} \u4e4b\u95f4",
    rangeCharErrorText: "\u5b57\u7b26\u6570\u5fc5\u987b\u5728 {0} \u5230 {1} \u4e4b\u95f4",
    rangeErrorText: "\u6570\u5b57\u5fc5\u987b\u5728 {0} \u5230 {1} \u4e4b\u95f4"
});
if (mini.Pager) mini.copyTo(mini.Pager.prototype, {
    firstText: "\u9996\u9875",
    prevText: "\u4e0a\u4e00\u9875",
    nextText: "\u4e0b\u4e00\u9875",
    lastText: "\u5c3e\u9875",
    pageInfoText: "\u6bcf\u9875 {0} \u6761,\u5171 {1} \u6761"
});
if (mini.DataGrid) mini.copyTo(mini.DataGrid.prototype, {
    emptyText: "\u6ca1\u6709\u8fd4\u56de\u7684\u6570\u636e"
});
if (mini.FileUpload) mini.FileUpload["prototype"].buttonText = "\u6d4f\u89c8...";
if (mini.HtmlFile) mini.HtmlFile["prototype"].buttonText = "\u6d4f\u89c8...";
if (window.mini.Gantt) {
    mini.GanttView.ShortWeeks = ["\u65e5", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d"];
    mini.GanttView.LongWeeks = ["\u661f\u671f\u65e5", "\u661f\u671f\u4e00", "\u661f\u671f\u4e8c", "\u661f\u671f\u4e09", "\u661f\u671f\u56db", "\u661f\u671f\u4e94", "\u661f\u671f\u516d"];
    mini.Gantt.PredecessorLinkType = [{
        ID: 0,
        Name: "\u5b8c\u6210-\u5b8c\u6210(FF)",
        Short: "FF"
    },
	{
	    ID: 1,
	    Name: "\u5b8c\u6210-\u5f00\u59cb(FS)",
	    Short: "FS"
	},
	{
	    ID: 2,
	    Name: "\u5f00\u59cb-\u5b8c\u6210(SF)",
	    Short: "SF"
	},
	{
	    ID: 3,
	    Name: "\u5f00\u59cb-\u5f00\u59cb(SS)",
	    Short: "SS"
	}];
    mini.Gantt.ConstraintType = [{
        ID: 0,
        Name: "\u8d8a\u65e9\u8d8a\u597d"
    },
	{
	    ID: 1,
	    Name: "\u8d8a\u665a\u8d8a\u597d"
	},
	{
	    ID: 2,
	    Name: "\u5fc5\u987b\u5f00\u59cb\u4e8e"
	},
	{
	    ID: 3,
	    Name: "\u5fc5\u987b\u5b8c\u6210\u4e8e"
	},
	{
	    ID: 4,
	    Name: "\u4e0d\u5f97\u65e9\u4e8e...\u5f00\u59cb"
	},
	{
	    ID: 5,
	    Name: "\u4e0d\u5f97\u665a\u4e8e...\u5f00\u59cb"
	},
	{
	    ID: 6,
	    Name: "\u4e0d\u5f97\u65e9\u4e8e...\u5b8c\u6210"
	},
	{
	    ID: 7,
	    Name: "\u4e0d\u5f97\u665a\u4e8e...\u5b8c\u6210"
	}];
    mini.copyTo(mini.Gantt, {
        ID_Text: "\u6807\u8bc6\u53f7",
        Name_Text: "\u4efb\u52a1\u540d\u79f0",
        PercentComplete_Text: "\u8fdb\u5ea6",
        Duration_Text: "\u5de5\u671f",
        Start_Text: "\u5f00\u59cb\u65e5\u671f",
        Finish_Text: "\u5b8c\u6210\u65e5\u671f",
        Critical_Text: "\u5173\u952e\u4efb\u52a1",
        PredecessorLink_Text: "\u524d\u7f6e\u4efb\u52a1",
        Work_Text: "\u5de5\u65f6",
        Priority_Text: "\u91cd\u8981\u7ea7\u522b",
        Weight_Text: "\u6743\u91cd",
        OutlineNumber_Text: "\u5927\u7eb2\u5b57\u6bb5",
        OutlineLevel_Text: "\u4efb\u52a1\u5c42\u7ea7",
        ActualStart_Text: "\u5b9e\u9645\u5f00\u59cb\u65e5\u671f",
        ActualFinish_Text: "\u5b9e\u9645\u5b8c\u6210\u65e5\u671f",
        WBS_Text: "WBS",
        ConstraintType_Text: "\u9650\u5236\u7c7b\u578b",
        ConstraintDate_Text: "\u9650\u5236\u65e5\u671f",
        Department_Text: "\u90e8\u95e8",
        Principal_Text: "\u8d1f\u8d23\u4eba",
        Assignments_Text: "\u8d44\u6e90\u540d\u79f0",
        Summary_Text: "\u6458\u8981\u4efb\u52a1",
        Task_Text: "\u4efb\u52a1",
        Baseline_Text: "\u6bd4\u8f83\u57fa\u51c6",
        LinkType_Text: "\u94fe\u63a5\u7c7b\u578b",
        LinkLag_Text: "\u5ef6\u9694\u65f6\u95f4",
        From_Text: "\u4ece",
        To_Text: "\u5230",
        Goto_Text: "\u8f6c\u5230\u4efb\u52a1",
        UpGrade_Text: "\u5347\u7ea7",
        DownGrade_Text: "\u964d\u7ea7",
        Add_Text: "\u65b0\u589e",
        Edit_Text: "\u7f16\u8f91",
        Remove_Text: "\u5220\u9664",
        Move_Text: "\u79fb\u52a8",
        ZoomIn_Text: "\u653e\u5927",
        ZoomOut_Text: "\u7f29\u5c0f",
        Deselect_Text: "\u53d6\u6d88\u9009\u62e9",
        Split_Text: "\u62c6\u5206\u4efb\u52a1"
    })
}