package testMiniC;

import generated.MiniCBaseListener;
import generated.MiniCParser;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by JongHun on 2016. 11. 4..
 */

public class MiniCPrintListener extends MiniCBaseListener {

   private ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();
   private HashMap<String, String> global_table = new HashMap<>();
   private HashMap<String, String> var_table;
   private int global_count = 1;
   private int var_count;
   private int jump_count;
   private String mainfirst;

   private static HashMap<String, ArrayList<String>> functionTable = new HashMap<>();
   private ArrayList<String> functionList;

   
   // program : decl+
   @Override
   public void exitProgram(MiniCParser.ProgramContext ctx) {
      // TODO Auto-generated method stub
      super.exitProgram(ctx);
      String decl = "";
      if (ctx.getChildCount() > 0) // decl이 하나 이상 존재할 때
         for (int i = 0; i < ctx.getChildCount(); i++)
            decl += newTexts.get(ctx.decl(i));
      decl += printSpace(11) + "bgn " + (global_count - 1) + "\n";
      decl += printSpace(11) + "ldp\n";
      decl += printSpace(11) + "call main\n";
      decl += printSpace(11) + "end";
      newTexts.put(ctx, decl);

      int numberChildOfMain = functionTable.get("main").size();

      System.out.println("Start The Line");
      for (int i = 0; i < numberChildOfMain; i++) {
    	 if(i ==0)
    		 mainfirst = functionTable.get("main").get(i);
         String first = functionTable.get("main").get(i);
         System.out.println();

         drawLine(first);
         System.out.println();
         if(i == (numberChildOfMain/2)-1)
             //System.out.println("main");

         depth = 0;
      }
      

       PrintFlow p = new PrintFlow();
       p.printRectangle();

   }

   int depth = 0;
   String backSpace = "     ";

   public void drawLine(String function) {
      depth++;
      
      // ex function은 3이라고 해봐
      
      
      if (functionTable.containsKey(function)) {
         int functionCount = functionTable.get(function).size();
         
         if (functionTable.get(function).size() == 1) {
        	 if(function.equals(mainfirst))
        		 System.out.printf("main- %14s" + "-", function);
        	 else
        		 System.out.printf("%20s" + "-", function);
            if(function.equals(functionTable.get(function).get(0))){
               System.out.printf("%20s" , function);
               System.out.print("(재귀)");
            }
            else{
               drawLine(functionTable.get(function).get(0));
            }
            
            depth--;

         } else if (functionTable.get(function).size() == functionCount) {

            System.out.printf("%20s" + "-", function);
            
            if(function.equals(functionTable.get(function).get(0))){
               System.out.printf("%20s" , function);
               System.out.print("(재귀)");
            }
            else{
               drawLine(functionTable.get(function).get(0));
            }
            
            for (int number = 1; number < functionCount; number++) {

               System.out.println();
               for (int i = 0; i < depth; i++) {
                  System.out.printf("%21s", backSpace);
               }
               
               if(function.equals(functionTable.get(function).get(number))){
                  System.out.printf("%20s" , function);
                  System.out.print("(재귀)");
               }
               
               else{
                  drawLine(functionTable.get(function).get(number));
               }
               
            }
            
            depth--;
         }

      } else {
    	 if(function.equals(mainfirst))
    		 System.out.printf("main- %14s" + "-", function);
    	 else
    		 System.out.printf("%20s" + "-", function);
         depth--;
      }

   }

   // decl : var_decl | fun_decl
   @Override
   public void exitDecl(MiniCParser.DeclContext ctx) {
      // TODO Auto-generated method stub
      super.exitDecl(ctx);
      String decl = "";
      if (ctx.getChildCount() == 1) {
         if (ctx.var_decl() != null) // decl이 var_decl인 경우
            decl += newTexts.get(ctx.var_decl());
         else // decl이 fun_decl인 경우
            decl += newTexts.get(ctx.fun_decl());
      }
      newTexts.put(ctx, decl);
   }

   // var_decl : type_spec IDENT ';' | type_spec IDENT '=' LITERAL
   // ';'|type_spec IDENT '[' LITERAL ']' ';'
   @Override
   public void exitVar_decl(MiniCParser.Var_declContext ctx) {
      // TODO Auto-generated method stub
      super.exitVar_decl(ctx);
      String decl = "";
      if (ctx.getChildCount() >= 3) {
         if (ctx.getChildCount() == 3) {
            decl += printSpace(11) + "sym 1 " + global_count + " " + 1;
            global_table.put(ctx.getChild(1).getText(), "1 " + global_count++);
         }

         else if (ctx.getChildCount() == 5) {
            decl += printSpace(11) + "sym 1 " + global_count + " " + 1 + "\n";
            decl += printSpace(11) + "ldc " + ctx.getChild(3).getText() + "\n";
            decl += printSpace(11) + "str 1 " + global_count;
            global_table.put(ctx.getChild(1).getText(), "1 " + global_count++);

         } else if (ctx.getChildCount() == 6) {
            int size = Integer.parseInt(ctx.getChild(3).getText());
            decl += printSpace(11) + "sym 1 " + global_count + " " + size; // 숫자
            global_table.put(ctx.getChild(1).getText(), "1 " + global_count);
            global_count += size;
         }
         decl += "\n";
      }

      newTexts.put(ctx, decl);
   }

   // type_spec : VOID | INT
   @Override
   public void exitType_spec(MiniCParser.Type_specContext ctx) {
      // TODO Auto-generated method stub
      super.exitType_spec(ctx);
      newTexts.put(ctx, ctx.getChild(0).getText());
   }

   // fun_decl : type_spec IDENT '(' params ')' compound_stmt
   @Override
   public void enterFun_decl(MiniCParser.Fun_declContext ctx) {
      // TODO Auto-generated method stub
      super.enterFun_decl(ctx);
      var_table = new HashMap<>();
      var_count = 1;
      jump_count = 0;

      functionList = new ArrayList<>();
      System.out.println(ctx.getChild(1).getText());
   }

   @Override
   public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
      // TODO Auto-generated method stub
      super.exitFun_decl(ctx);
      String stmt = "";
      if (ctx.getChildCount() == 6) {
         stmt += ctx.getChild(1).getText() + printSpace(11 - ctx.getChild(1).getText().length()) + "proc "
               + (var_count - 1) + " 2 2" + "\n";
         stmt += newTexts.get(ctx.compound_stmt()); // compound_stmt
         stmt += printSpace(11) + "end\n";
         newTexts.put(ctx, stmt);
      }

      HashSet hs = new HashSet();
      hs.addAll(functionList); // ArryList를 HashSet에 담는다.
      functionList.clear(); // 기존 ArrayList를 비운다.
      functionList.addAll(hs); // HashSet을 ArrayList에 다시 담는다.
      functionTable.put(ctx.getChild(1).getText(), functionList);

   }

   // params : param (',' param)* | VOID
   @Override
   public void exitParams(MiniCParser.ParamsContext ctx) {
      // TODO Auto-generated method stub
      super.exitParams(ctx);

   }

   // param : type_spec IDENT | type_spec IDENT '[' ']'
   @Override
   public void exitParam(MiniCParser.ParamContext ctx) {
      // TODO Auto-generated method stub
      super.exitParam(ctx);

      if (ctx.getChildCount() >= 2) {
         var_table.put(ctx.IDENT().getText(), "2 " + var_count++);
      }
   }

   // stmt : expr_stmt | compound_stmt | if_stmt | while_stmt | return_stmt
   @Override
   public void exitStmt(MiniCParser.StmtContext ctx) {
      // TODO Auto-generated method stub
      super.exitStmt(ctx);
      String stmt = "";
      if (ctx.getChildCount() > 0) {
         if (ctx.expr_stmt() != null) // expr_stmt일 때
            stmt += newTexts.get(ctx.expr_stmt());
         else if (ctx.compound_stmt() != null) // compound_stmt일 때
            stmt += newTexts.get(ctx.compound_stmt());
         else if (ctx.if_stmt() != null) // if_stmt일 때
            stmt += newTexts.get(ctx.if_stmt());
         else if (ctx.while_stmt() != null) // while_stmt일 때
            stmt += newTexts.get(ctx.while_stmt());
         else // return_stmt일 때
            stmt += newTexts.get(ctx.return_stmt());
      }
      newTexts.put(ctx, stmt);
   }

   // expr_stmt : expr ';'
   @Override
   public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
      // TODO Auto-generated method stub
      super.exitExpr_stmt(ctx);
      String stmt = "";
      if (ctx.getChildCount() == 2)
         stmt += newTexts.get(ctx.expr()); // expr

      newTexts.put(ctx, stmt);
   }

   // while_stmt : WHILE '(' expr ')' stmt
   @Override
   public void enterWhile_stmt(MiniCParser.While_stmtContext ctx) {
      // TODO Auto-generated method stub
      super.enterWhile_stmt(ctx);

   }

   @Override
   public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
      // TODO Auto-generated method stub
      super.exitWhile_stmt(ctx);
      String stmt = "";
      if (ctx.getChildCount() == 5) {
         stmt += "$$" + jump_count++ + printSpace(11 - ("$$" + jump_count).length()) + "nop\n";
         stmt += newTexts.get(ctx.expr()); // expr
         stmt += printSpace(11) + "fjp $$" + jump_count + "\n";
         stmt += newTexts.get(ctx.stmt()); // stmt
         stmt += printSpace(11) + "ujp $$" + (jump_count - 1) + "\n";
         stmt += "$$" + jump_count++ + printSpace(11 - ("$$" + jump_count).length()) + "nop\n";
      }
      newTexts.put(ctx, stmt);
   }

   // compound_stmt : '{' local_decl* stmt* '}'
   @Override
   public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
      // TODO Auto-generated method stub
      super.exitCompound_stmt(ctx);
      String stmt = "";
      int local_i = 0, stmt_i = 0;
      if (ctx.getChildCount() >= 2) {
         for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            if (ctx.local_decl().contains(ctx.getChild(i)))
               stmt += newTexts.get(ctx.local_decl(local_i++));
            else
               stmt += newTexts.get(ctx.stmt(stmt_i++));
         }

      }
      newTexts.put(ctx, stmt);
   }

   // local_decl : type_spec IDENT ';' | type_spec IDENT '=' LITERAL
   // ';'|type_spec IDENT '[' LITERAL ']' ';'
   @Override
   public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
      // TODO Auto-generated method stub
      super.exitLocal_decl(ctx);
      String decl = "";
      if (ctx.getChildCount() >= 3) {

         if (ctx.getChildCount() == 3) {
            decl += printSpace(11) + "sym 2 " + var_count + " 1";
            var_table.put(ctx.getChild(1).getText(), "2 " + var_count++);
         }
         if (ctx.getChildCount() == 5) {
            decl += printSpace(11) + "sym 2 " + var_count + " 1\n";
            decl += printSpace(11) + "ldc " + ctx.getChild(3).getText() + "\n";
            decl += printSpace(11) + "str 2 " + var_count;
            var_table.put(ctx.getChild(1).getText(), "2 " + var_count++);
         }

         if (ctx.getChildCount() == 6) {
            int size = Integer.parseInt(ctx.getChild(3).getText());
            decl += printSpace(11) + "sym 2 " + var_count + " " + size; // 숫자
            var_table.put(ctx.getChild(1).getText(), "2 " + var_count);
            var_count += size;
         }
         decl += "\n";
      }
      newTexts.put(ctx, decl);
   }

   // if_stmt : IF '(' expr ')' stmt | IF '(' expr ')' stmt ELSE stmt;
   @Override
   public void enterIf_stmt(MiniCParser.If_stmtContext ctx) {
      // TODO Auto-generated method stub
      super.enterIf_stmt(ctx);

   }

   @Override
   public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
      // TODO Auto-generated method stub
      super.exitIf_stmt(ctx);
      String stmt = "";
      if (ctx.getChildCount() >= 5) {

         stmt += newTexts.get(ctx.expr()); // expr
         stmt += printSpace(11) + "fjp $$" + jump_count + "\n";
         stmt += newTexts.get(ctx.stmt(0)); // stmt

         if (ctx.getChildCount() == 7) {
            stmt += printSpace(11) + "ujp $$" + (jump_count + 1) + "\n";
            stmt += "$$" + jump_count++ + printSpace(11 - ("$$" + jump_count).length()) + "nop\n";
            stmt += newTexts.get(ctx.stmt(1)); // stmt
         }

         stmt += "$$" + jump_count++ + printSpace(11 - ("$$" + jump_count).length()) + "nop\n";

         newTexts.put(ctx, stmt);
      }

   }

   // return_stmt : RETURN ';' | RETURN expr ';'
   @Override
   public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
      // TODO Auto-generated method stub
      super.exitReturn_stmt(ctx);
      String stmt = "";
      if (ctx.getChildCount() >= 2) {

         if (ctx.getChildCount() == 2) {
            stmt += printSpace(11) + "ret"; // RETURN
         }
         if (ctx.getChildCount() == 3) {
            stmt += newTexts.get(ctx.expr());
            stmt += printSpace(11) + "retv";
         }
         stmt += "\n";
      }
      newTexts.put(ctx, stmt);
   }

   @Override
   public void enterExpr(MiniCParser.ExprContext ctx) {
      super.enterExpr(ctx);
      if (ctx.getChildCount() == 4) {
         if (ctx.args() != null) {// args
            functionList.add(ctx.getChild(0).getText());

            System.out.println("call " + ctx.getChild(0).getText());
         }
      }
   }

   // expr
   @Override
   public void exitExpr(MiniCParser.ExprContext ctx) {
      // TODO Auto-generated method stub
      super.exitExpr(ctx);
      String expr = "";
      if (ctx.getChildCount() > 0) {
         // IDENT | LITERAL일 경우
         if (ctx.getChildCount() == 1) {
            if (isLocal(ctx.getChild(0).getText()))
               expr += printSpace(11) + "lod " + var_table.get(ctx.getChild(0).getText());
            else if (isGlobal(ctx.getChild(0).getText()))
               expr += printSpace(11) + "lod " + global_table.get(ctx.getChild(0).getText());
            else
               expr += printSpace(11) + "ldc " + ctx.getChild(0).getText();
            expr += "\n";
         }
         // '!' expr | '-' expr | '+' expr일 경우
         else if (ctx.getChildCount() == 2) {
            expr += newTexts.get(ctx.expr(0));
            String op = operation(ctx.getChild(0).getText());
            if (op.equals("inc") || op.equals("dec")) {
               expr += printSpace(11) + "" + op + "\n";
               if (isLocal(ctx.expr(0).getText()))
                  expr += printSpace(11) + "str " + var_table.get(ctx.expr(0).getText());
               else
                  expr += printSpace(11) + "str " + global_table.get(ctx.expr(0).getText());
            }
            if (op.equals("sub"))
               expr += printSpace(11) + "neg";

            if (op.equals("notop")) {
               expr += printSpace(11) + "notop";
            }
            expr += "\n";
         } else if (ctx.getChildCount() == 3) {
            // '(' expr ')'
            if (ctx.getChild(0).getText().equals("(")) {
               expr += newTexts.get(ctx.expr(0));
            }
            // IDENT '=' expr
            else if (ctx.getChild(1).getText().equals("=")) {
               expr += newTexts.get(ctx.getChild(2));
               if (isLocal(ctx.getChild(0).getText()))
                  expr += printSpace(11) + "str " + var_table.get(ctx.getChild(0).getText()) + "\n";
               else
                  expr += printSpace(11) + "str " + global_table.get(ctx.getChild(0).getText()) + "\n";
            }
            // binary operation
            else {

               if (isLocal(ctx.expr(0).getText()))
                  expr += printSpace(11) + "lod " + var_table.get(ctx.expr(0).getText()) + "\n";
               else if (isGlobal(ctx.expr(0).getText()))
                  expr += printSpace(11) + "lod " + global_table.get(ctx.expr(0).getText()) + "\n";
               else
                  expr += printSpace(11) + "ldc " + ctx.expr(0).getText() + "\n";

               if (isLocal(ctx.expr(1).getText()))
                  expr += printSpace(11) + "lod " + var_table.get(ctx.expr(1).getText()) + "\n";
               else if (isGlobal(ctx.expr(1).getText()))
                  expr += printSpace(11) + "lod " + global_table.get(ctx.expr(1).getText()) + "\n";
               else
                  expr += printSpace(11) + "ldc " + ctx.expr(1).getText() + "\n";
               expr += printSpace(11) + "" + operation(ctx.getChild(1).getText());
               expr += "\n";
            }

         }
         // IDENT '(' args ')' | IDENT '[' expr ']'일 경우
         else if (ctx.getChildCount() == 4) {

            if (ctx.args() != null) // args
            {
               expr += printSpace(11) + "ldp\n";
               expr += newTexts.get(ctx.args());
               expr += printSpace(11) + "call " + ctx.getChild(0).getText();

            } else // expr
            {
               expr += newTexts.get(ctx.expr(0));
               if (isLocal(ctx.getChild(0).getText()))
                  expr += printSpace(11) + "lda " + var_table.get(ctx.getChild(0).getText()) + "\n";
               else
                  expr += printSpace(11) + "lda " + global_table.get(ctx.getChild(0).getText()) + "\n";
               expr += printSpace(11) + "add";
            }
            expr += "\n";
         }
         // IDENT '[' expr ']' '=' expr
         else {
            expr += newTexts.get(ctx.expr(0)); // expr
            if (isLocal(ctx.getChild(0).getText()))
               expr += printSpace(11) + "lda " + var_table.get(ctx.getChild(0).getText()) + "\n";
            else
               expr += printSpace(11) + "lda " + global_table.get(ctx.getChild(0).getText()) + "\n";
            expr += printSpace(11) + "add\n";
            expr += newTexts.get(ctx.expr(1)); // expr
            expr += printSpace(11) + "sti";
            expr += "\n";
         }

         newTexts.put(ctx, expr);
      }
   }

   // args : expr (',' expr)* | ;
   @Override
   public void exitArgs(MiniCParser.ArgsContext ctx) {
      // TODO Auto-generated method stub
      super.exitArgs(ctx);
      String args = "";
      if (ctx.getChildCount() >= 0) {
         for (int i = 0; i < ctx.getChildCount(); i++) {
            if (i % 2 == 0)
               args += newTexts.get(ctx.expr(i / 2)); // expr

         }
      }
      newTexts.put(ctx, args);
   }

   private String operation(String op) {
      switch (op) {
      case "+":
         return "add";
      case "-":
         return "sub";
      case "*":
         return "mult";
      case "/":
         return "div";
      case "%":
         return "mod";
      case "++":
         return "inc";
      case "--":
         return "dec";
      case "==":
         return "eq";
      case "<=":
         return "le";
      case "<":
         return "lt";
      case ">=":
         return "ge";
      case ">":
         return "gt";
      case "!":
         return "notop";
      }
      return null;
   }

   private boolean isLocal(String name) {
      return var_table.get(name) != null;
   }

   private boolean isGlobal(String name) {
      return global_table.get(name) != null;
   }

   private String printSpace(int len) {
      String space = "";
      for (int i = 0; i < len; i++)
         space += " ";
      return space;
   }

}