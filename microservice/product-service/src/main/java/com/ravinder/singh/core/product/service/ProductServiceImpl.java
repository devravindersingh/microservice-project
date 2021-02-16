package com.ravinder.singh.core.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ravinder.singh.api.core.product.Product;
import com.ravinder.singh.api.core.product.ProductService;
import com.ravinder.singh.util.http.ServiceUtil;



@RestController
public class ProductServiceImpl implements ProductService{
	
	private final ServiceUtil serviceUtil;
	
	@Autowired
	public ProductServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}

	@Override
	public Product getProduct(int productId) {
		return new Product(productId, "Shampoo", 25, serviceUtil.getServiceAddress());
	}


}
