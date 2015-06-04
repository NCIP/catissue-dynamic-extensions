package edu.common.dynamicextensions.query;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

//import edu.QueryTester;
import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.MultiSelectControl;
import edu.common.dynamicextensions.domain.nui.SubFormControl;
import edu.common.dynamicextensions.napi.VersionedContainer;
import edu.common.dynamicextensions.napi.impl.VersionedContainerImpl;
import edu.common.dynamicextensions.query.ast.ArithExpressionNode;
import edu.common.dynamicextensions.query.ast.DateDiffFuncNode;
import edu.common.dynamicextensions.query.ast.ExpressionNode;
import edu.common.dynamicextensions.query.ast.FieldNode;
import edu.common.dynamicextensions.query.ast.FilterExpressionNode;
import edu.common.dynamicextensions.query.ast.FilterExpressionNode.Op;
import edu.common.dynamicextensions.query.ast.FilterNode;
import edu.common.dynamicextensions.query.ast.FilterNodeMarker;
import edu.common.dynamicextensions.query.ast.QueryExpressionNode;
import edu.common.dynamicextensions.query.ast.SelectListNode;

public class QueryCompiler
{
    private int tabCnt;
    
    private String rootFormName;
    
    private String query;
    
    private String restriction;
    
    private QueryExpressionNode queryExpr;
    
    private JoinTree queryJoinTree;
    
    private int numQueries;
    
    private boolean vcEnabled;
    
    public QueryCompiler(String rootFormName, String query) {
        this(rootFormName, query, null);
    }
    
    public QueryCompiler(String rootFormName, String query, String restriction) {
        this.rootFormName = rootFormName;
        this.query = query;
        this.restriction = restriction;
    }
    
    public QueryCompiler enabledVersionedForms(boolean vcEnabled) {
    	this.vcEnabled = vcEnabled;
    	return this;
    }
    
    public void compile() {
        QueryParser queryParser = new QueryParser(query);
        queryExpr = queryParser.getQueryAst();
        addRestrictions();
        queryJoinTree = buildJoinTree(queryExpr);
    }

    public QueryExpressionNode getQueryExpr() {
        return queryExpr;
    }

    public JoinTree getQueryJoinTree() {
        return queryJoinTree;
    }
    
    private void addRestrictions() {
        if (restriction == null) {
            return;
        }
        
        FilterExpressionNode newFilter = FilterExpressionNode.andExpr(
                FilterExpressionNode.parenExpr(queryExpr.getFilterExpr()), 
                new QueryParser(restriction).getQueryAst().getFilterExpr());
        queryExpr.setFilterExpr(newFilter);
    }

    private JoinTree buildJoinTree(QueryExpressionNode queryExpr) {
        Map<String, JoinTree> joinMap = analyzeExpr(queryExpr);
        JoinTree rootTree = joinMap.get("0." + rootFormName);
        
        if (rootTree == null) {
            Container rootForm = getContainer(rootFormName);
            rootTree = new JoinTree(rootForm, "t" + tabCnt++);
        }
        
        for (Map.Entry<String, JoinTree> formTreeEntry : joinMap.entrySet()) {
            if (formTreeEntry.getKey().equals("0." + rootFormName)) {
                continue;
            }
          
            String formLookupName = formTreeEntry.getKey();            
            JoinTree childTree = formTreeEntry.getValue();
            
            String dest = formLookupName.substring(formLookupName.indexOf(".") + 1); // Earlier childTree.getFormName();
            Path path = PathConfig.getInstance().getPath(rootFormName, dest);
            if (path == null) {
                throw new RuntimeException("No path between root form " + rootFormName + " and " + dest);
            }
            
            int queryId = Integer.parseInt(formTreeEntry.getKey().split("\\.")[0]);
            createPath(queryId, rootTree, childTree, path);
        }

        return rootTree;
    }
    
    private void createPath(int queryId, JoinTree from, JoinTree to, Path path) {
        JoinTree current = createPath(queryId, from, path);
    	//JoinTree current = from;
        
        for (PathLink link : path.getLinks()) {
            if (link.getRefTab() == null && to.getParent() == null) {            	
                to.setParent(current);
                to.setForeignKey(link.getRefTabKey());
                to.setParentKey(link.getKey());
                //current.addChild(to.getTab(), to); // TODO: PAND Fix
                current.addChild(to.getAlias(), to);
                break;
            }
            
            JoinTree child = current.getChild(link.getRefTab());            
            if(child == null) {
                child = new JoinTree(link.getRefTab(), "t" + tabCnt++);
                child.setParent(current);
                child.setForeignKey(link.getRefTabKey());
                child.setParentKey(link.getKey());
                current.addChild(child.getAlias(), child); // Earlier: name = child.getTab()                
            }
            current = child;
        }
        
        current.setInnerJoin(path.isWildCard());
    }

    private JoinTree createPath(int queryId, JoinTree formTree, Path path) {
    	String startField = path.getStartField();
    	
        if (startField == null) {
            return formTree;
        }
        
        String fieldNameParts[] = startField.split("\\.");
        for (int i = 0; i < fieldNameParts.length; i++) {
            JoinTree child = formTree.getChild(queryId + "." + fieldNameParts[i]);
            if (child == null) {
            	child = formTree.getChild("0." + fieldNameParts[i]);
            }            	

            if (child == null) {
            	SubFormControl sfCtrl = (SubFormControl)formTree.getForm().getControlByUdn(fieldNameParts[i]);
                JoinTree sfTree = getSubFormTree(formTree, sfCtrl);
                formTree.addChild("0." + fieldNameParts[i], sfTree); // PAND fix "0."
                formTree = sfTree;
            } else {
                formTree = child;
            }
        }

        return formTree;
    }

    private JoinTree getSubFormTree(JoinTree parentNode, SubFormControl sfCtrl) {
        JoinTree sfTree = new JoinTree();
        sfTree.setForm(sfCtrl.getSubContainer());
        sfTree.setTab(sfCtrl.getSubContainer().getDbTableName());
        sfTree.setParent(parentNode);
        sfTree.setAlias((new StringBuilder()).append("t").append(tabCnt++).toString());
        sfTree.setParentKey(sfCtrl.getParentKey());
        sfTree.setForeignKey(sfCtrl.getForeignKey());
        return sfTree;
    }

    private JoinTree getFieldTree(JoinTree parentNode, MultiSelectControl field) {
        JoinTree fieldTree = new JoinTree();
        fieldTree.setField(field);
        fieldTree.setTab(field.getTableName());
        fieldTree.setParent(parentNode);
        fieldTree.setAlias((new StringBuilder()).append("t").append(tabCnt++).toString());
        fieldTree.setParentKey(field.getParentKey());
        fieldTree.setForeignKey(field.getForeignKey());
        return fieldTree;
    }
    
    private Map<String, JoinTree> analyzeExpr(QueryExpressionNode expr) {
        Map<String, JoinTree> joinMap = new HashMap<String, JoinTree>();        
        analyzeFilterNodeMarker(0, expr.getFilterExpr(), joinMap);
        expr.setSelectList(analyzeSelectList(expr.getSelectList(), joinMap));
        return joinMap;
    }
    
    private void analyzeFilterNodeMarker(int queryId, FilterNodeMarker expr, Map<String, JoinTree> joinMap) {
        if (expr instanceof FilterNode) {
            FilterNode filter = (FilterNode)expr;
            analyzeFilterNode(queryId, filter, joinMap);
        } else {
            FilterExpressionNode subExpr = (FilterExpressionNode)expr;
            for (FilterNodeMarker childExpr : subExpr.getOperands()) {
                if (subExpr.getOperator() == Op.PAND) {
                    queryId++;
                    this.numQueries++;
                }
                
                analyzeFilterNodeMarker(queryId, childExpr, joinMap);
            }
        }
    }

    private void analyzeFilterNode(int queryId, FilterNode filter, Map<String, JoinTree> joinMap) {
        analyzeExpressionNode(queryId, filter.getLhs(), joinMap);
        analyzeExpressionNode(queryId, filter.getRhs(), joinMap);       
    }
    
    private void analyzeArithExpressionNode(int queryId, ArithExpressionNode expr, Map<String, JoinTree> joinMap) {
        analyzeExpressionNode(queryId, expr.getLeftOperand(), joinMap);
        analyzeExpressionNode(queryId, expr.getRightOperand(), joinMap);        
    }
    
    private void analyzeDateDiffFuncNode(int queryId, DateDiffFuncNode dateDiff, Map<String, JoinTree> joinMap) {
        analyzeExpressionNode(queryId, dateDiff.getLeftOperand(), joinMap);
        analyzeExpressionNode(queryId, dateDiff.getRightOperand(), joinMap);
    }
    
    private void analyzeExpressionNode(int queryId, ExpressionNode exprNode, Map<String, JoinTree> joinMap) {
        if (exprNode instanceof FieldNode) {
            analyzeField(queryId, (FieldNode)exprNode, joinMap);
        } else if (exprNode instanceof ArithExpressionNode) {
            analyzeArithExpressionNode(queryId, (ArithExpressionNode)exprNode, joinMap);
        } else if (exprNode instanceof DateDiffFuncNode) {
            analyzeDateDiffFuncNode(queryId, (DateDiffFuncNode)exprNode, joinMap);
        }       
    }
    
    private SelectListNode analyzeSelectList(SelectListNode selectList, Map<String, JoinTree> joinMap) {
        Map<ExpressionNode, Set<ExpressionNode>> selectElementMap = new LinkedHashMap<ExpressionNode, Set<ExpressionNode>>();
        
        for (ExpressionNode element : selectList.getElements()) {
          selectElementMap.put(element, new LinkedHashSet<ExpressionNode>());
        }
        
        for (ExpressionNode element : selectElementMap.keySet()) {
              for (int i = 0; i <= numQueries; ++i) {
                  ExpressionNode selectNode = element.copy();
                  if (analyzeSelectExpressionNode(i, selectNode, joinMap, true)) {
                      selectElementMap.get(element).add(selectNode);
                  }              
              }
        }
        
        for (ExpressionNode element : selectElementMap.keySet()) {
            if (selectElementMap.get(element).isEmpty()) {
                ExpressionNode selectNode = element.copy();
                analyzeSelectExpressionNode(0, selectNode, joinMap, false);
                selectElementMap.get(element).add(selectNode);
            }
        }
        
        SelectListNode finalSelectList = new SelectListNode();
        boolean endOfElements = false;
        while (!endOfElements) {
            endOfElements = true;
            
            for (Set<ExpressionNode> expressionNodes : selectElementMap.values()) {
                if (expressionNodes.isEmpty()) {
                    continue;
                }
                
                ExpressionNode element = expressionNodes.iterator().next();
                expressionNodes.remove(element);
                finalSelectList.addElement(element);
                endOfElements = false;
            }           
        }
        
        return finalSelectList;     
    }

    private boolean analyzeSelectArithExpressionNode(int queryId, ArithExpressionNode expr, Map<String, JoinTree> joinMap, boolean failIfAbsent) {
        boolean result = analyzeSelectExpressionNode(queryId, expr.getLeftOperand(), joinMap, failIfAbsent);
        if (result || !failIfAbsent) {
            result = analyzeSelectExpressionNode(queryId, expr.getRightOperand(), joinMap, failIfAbsent); 
        }
        
        return (result || !failIfAbsent);               
    }
    
    private boolean analyzeSelectDateDiffFuncNode(int queryId, DateDiffFuncNode dateDiff, Map<String, JoinTree> joinMap, boolean failIfAbsent) {
        boolean result = analyzeSelectExpressionNode(queryId, dateDiff.getLeftOperand(), joinMap, failIfAbsent);
        if (result || !failIfAbsent) {
            result = analyzeSelectExpressionNode(queryId, dateDiff.getRightOperand(), joinMap, failIfAbsent);
        }
        
        return (result || !failIfAbsent);
    }

    private boolean analyzeSelectExpressionNode(int queryId, ExpressionNode exprNode, Map<String, JoinTree> joinMap, boolean failIfAbsent) {
        if (exprNode instanceof FieldNode) {
            return analyzeField(queryId, (FieldNode)exprNode, joinMap, failIfAbsent);
        } else if (exprNode instanceof ArithExpressionNode) {
            return analyzeSelectArithExpressionNode(queryId, (ArithExpressionNode)exprNode, joinMap, failIfAbsent);
        } else if (exprNode instanceof DateDiffFuncNode) {
            return analyzeSelectDateDiffFuncNode(queryId, (DateDiffFuncNode)exprNode, joinMap, failIfAbsent);
        } else {
            return !failIfAbsent; // literal nodes
        }
    }
    
    private boolean analyzeField(int queryId, FieldNode field, Map<String, JoinTree> joinMap) {
        return analyzeField(queryId, field, joinMap, false);
    }
    
    private boolean analyzeField(int queryId, FieldNode field, Map<String, JoinTree> joinMap, boolean failIfAbsent) {
        String formLookupName = "";
        String[] fieldNameParts = field.getName().split("\\.");
        String[] captions = new String[fieldNameParts.length];
        
        String formName = fieldNameParts[0];
        if (formName.equals(rootFormName)) {
            formLookupName = "0." + formName;
        } else {
            formLookupName = queryId + "." + formName;
        }
                
        JoinTree formTree = joinMap.get(formLookupName);
        if (formTree == null && failIfAbsent) {
            return false;
        }
        
        Container form = null;        
        if (formTree == null) {
            form = getContainer(formName);
            if(form == null) {
                throw new RuntimeException("Invalid field " + field.getName() + " referring to non-existing form: " + formName);
            }
                        
            formTree = new JoinTree(form, "t" + tabCnt++);
            joinMap.put(formLookupName, formTree);
        } else {
            form = formTree.getForm();
        }        
        captions[0] = form.getCaption();
                
        Control ctrl = form.getControlByUdn(fieldNameParts[1]);
        if(ctrl instanceof SubFormControl && fieldNameParts.length > 2) {
            for(int i = 1; i < fieldNameParts.length - 1; i++) {
                ctrl = form.getControlByUdn(fieldNameParts[i]);
                
                if(!(ctrl instanceof SubFormControl)) {
                    throw new RuntimeException("Invalid filter referring to invalid field: " + field.getName());
                }
                
                SubFormControl sfCtrl = (SubFormControl)ctrl;
                JoinTree sfTree = formTree.getChild(queryId + "." + sfCtrl.getName());
                if (sfTree == null && failIfAbsent) {
                    return false;
                }
                
                if(sfTree == null) {
                    sfTree = getSubFormTree(formTree, sfCtrl);
                    formTree.addChild(queryId + "." + sfCtrl.getName(), sfTree);
                }
                
                formTree = sfTree;
                form = sfCtrl.getSubContainer();
                captions[i] = form.getCaption();
            }

            ctrl = form.getControlByUdn(fieldNameParts[fieldNameParts.length - 1]);
        }
        
        if(ctrl == null) {
            throw new RuntimeException("Invalid filter referring to invalid field: " + field.getName());
        }
        
        String tabAlias = formTree.getAlias();
        if (ctrl instanceof MultiSelectControl) {
            JoinTree fieldTree = formTree.getChild(queryId + "." + ctrl.getName());
            if (fieldTree == null && failIfAbsent) {
                return false;
            }
            
            if (fieldTree == null) {
                MultiSelectControl msField = (MultiSelectControl)ctrl;
                fieldTree = getFieldTree(formTree, msField);
                formTree.addChild(queryId + "." + ctrl.getName(), fieldTree);
            }
            
            tabAlias = fieldTree.getAlias();
        }
        
        captions[captions.length - 1] = ctrl.getCaption();
        
        field.setCtrl(ctrl);
        field.setTabAlias(tabAlias);
        field.setNodeCaptions(captions);
        return true;
    }    
    
    private Container getContainer(String name) {
    	Container container = null;
    	
    	if (vcEnabled) {
    		// When VC enabled, name refers to the versioned form name 
    		// i.e. form name field of dyextn_forms table
    		VersionedContainer vc = new VersionedContainerImpl();
    		container = vc.getContainer(name);
    	} else {
    		// otherwise, name refers to container name
    		// i.e. name field of dyextn_containers
    		container = Container.getContainer(name);
    	}
    	
    	return container;
    }
}
