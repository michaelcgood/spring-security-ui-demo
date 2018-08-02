package com.axiomatics.configuration;

import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import com.axiomatics.repository.UserRepository;
import com.axiomatics.spring.security.xacml.AbstractXACMLWebSecurityExpressionRoot;

@Component
@Lazy
public class XACMLWebSecurityExpressionRoot extends AbstractXACMLWebSecurityExpressionRoot {
	static Logger log = LoggerFactory.getLogger(XACMLWebSecurityExpressionRoot.class);

	public XACMLWebSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
		super(a, fi);
	}

	@Autowired
	UserRepository userRepository;

	/*
	 * Details about the http request can be retrieved using the getters provided by
	 * AbstractXACMLWebSecurityExpressionRoot
	 */
	HttpServletRequest request = super.getRequest();
	String fullRequesrUrl = super.getFullRequestUrl();

	/**
	 * Implementation of method called by all XACML decision instance Can be used to
	 * set attribute details that are generic to the decision scope.
	 * <p>
	 * The idea is that this is the place to set extra attributes that might be
	 * needed for the XACML decision making process and is not already present in
	 * the AbstractXACMLWebSecurityExpressionRoot implementation.
	 * <p>
	 * Make sure to add to all the four arrayList.
	 */
	@Override
	public void setDefaultAttributes() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		attrCatAry.add("SUBJECT");
		attrTypeAry.add("STRING");
		attrIdAry.add("com.axiomatics.emailAddress");
		attrValAry.add(auth.getName());
		Collection<?> authorities = auth.getAuthorities();
		for (Iterator<?> roleIter = authorities.iterator(); roleIter.hasNext();) {
			GrantedAuthority grantedAuthority = (GrantedAuthority) roleIter.next();
			attrCatAry.add("SUBJECT");
			attrTypeAry.add("STRING");
			attrIdAry.add("role");
			attrValAry.add(grantedAuthority.getAuthority());
		}
	}

	/**
	 * Implementation of method that is called when XACML is used in taglib for UI
	 * display control
	 */
	@Override
	public void uiDecisionSetDefaultAttributes() {
		// Add default attributes for decisions related to UI elements access
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		attrCatAry.add("SUBJECT");
		attrTypeAry.add("INTEGER");
		attrIdAry.add("com.axiomatics.seniority");
		Integer seniority = null;
		try {
			seniority = userRepository.findByEmail(auth.getName()).getSeniority();
		} catch (Exception e) {
			log.info(e.toString());
		}
		attrValAry.add(seniority);
		log.info("UI DECISION SET DEFAULT ATTRIBUTE WORKING...");

	}

	/**
	 * Implementation of method that is called when XACML is used to control access
	 * to URLs. We are not using this method in this tutorial.
	 */
	@Override
	public void urlDecisionSetDefaultAttributes() {
		// TODO Add default attributes for decisions related to URL access

	}

}