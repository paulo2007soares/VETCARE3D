package br.com.vetcare.security;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class AuthInterceptor implements HandlerInterceptor {
 @Override public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler) throws Exception {
  String path=req.getRequestURI();
  if(!path.startsWith("/api/") || path.startsWith("/api/auth/")) return true;
  HttpSession session=req.getSession(false);
  if(session==null || session.getAttribute("USER_ID")==null){
   res.setStatus(401);res.setContentType("application/json");
   res.getWriter().write("{\"erro\":\"Sessão expirada. Faça login novamente.\"}");return false;
  }
  String role=String.valueOf(session.getAttribute("ROLE"));
  if(path.startsWith("/api/prontuarios") && !(role.equals("ADMIN")||role.equals("VET"))){
   res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"erro\":\"Acesso restrito à equipe veterinária.\"}");return false;
  }
  if(path.startsWith("/api/estoque") && !(role.equals("ADMIN")||role.equals("STOCK"))){
   res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"erro\":\"Acesso restrito ao estoque.\"}");return false;
  }
  if(path.startsWith("/api/financeiro") && !(role.equals("ADMIN")||role.equals("FINANCE"))){
   res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"erro\":\"Acesso restrito ao financeiro.\"}");return false;
  }
  return true;
 }
}
