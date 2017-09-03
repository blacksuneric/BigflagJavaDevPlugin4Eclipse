package com.bigflag.javadevplugin.views;


import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class HelloWorldView extends ViewPart {
	private Label label;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		this.label=new Label(parent, SWT.WRAP);
		label.setText("这是一个测试");
		IExtensionRegistry registry= Platform.getExtensionRegistry();
		IExtensionPoint point=registry.getExtensionPoint("org.eclipse.ui.views");
		IExtension[] extensions=point.getExtensions();
		StringBuilder sb=new StringBuilder();
		for(IExtension ext:extensions)
		{
			sb.append(ext.getSimpleIdentifier());
		}
		label.setText(sb.toString());
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	
}