package hilltop.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import hilltop.services.Authentication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@WebFilter(filterName = "TokenFilter", urlPatterns = { "/rest/*" })
public class JWTFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		
		if (req instanceof HttpServletRequest) {
			String url = ((HttpServletRequest) req).getRequestURL().toString();
			if (url.contains("/jwt")) {
				try {
					String token = getToken((HttpServletRequest)req);
					if (token == null || token.trim().equals("")) {
						System.out.println("null token");
						return;
					}
				    Jwts.parser().setSigningKey(Authentication.SECRET_KEY).parseClaimsJws(token);
				    System.out.println("trust");
				    chain.doFilter(req, res);
				} catch (SignatureException e) {
					
				} catch (ServletException se) {
					
				}
			} else {
				System.out.println("no key required");
				chain.doFilter(req, res);
			}
		}

	}
	
	private String getToken(HttpServletRequest httpRequest) throws ServletException {
	      String token = null;
	        final String authorizationHeader = httpRequest.getHeader("authorization");
	        if (authorizationHeader == null) {
	            throw new ServletException("Unauthorized: No Authorization header was found");
	        }

	        String[] parts = authorizationHeader.split(" ");
	        if (parts.length != 2) {
	            throw new ServletException("Unauthorized: Format is Authorization: Bearer [token]");
	        }

	        String scheme = parts[0];
	        String credentials = parts[1];

	        Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
	        if (pattern.matcher(scheme).matches()) {
	            token = credentials;
	        }
	        return token;
	    }

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
