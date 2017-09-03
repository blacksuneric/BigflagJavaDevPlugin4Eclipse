package com.bigflag.javadevplugin.handlers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.bigflag.javadevplugin.tools.DevTools;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CreateFluentAPIHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public CreateFluentAPIHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFile file= (IFile) part.getEditorInput().getAdapter(IFile.class);
		InputStream in;
		try {
			in = file.getContents();
			BufferedInputStream br = new BufferedInputStream(in);
			byte[] bs = new byte[1024];
			// ArrayList<Byte> byteArray=new ArrayList<Byte>();
			br.read(bs);
//			MessageDialog.openInformation(window.getShell(), "This is a test", new String(bs, "utf8"));
			String fileContent=new String(bs,"utf8");
			String fluentApiBean=DevTools.createFluentApiForBean(fileContent);
			
			InputStream sbs = new ByteArrayInputStream(fluentApiBean.getBytes());
			file.setContents(sbs, IResource.FORCE, null);
			
			
			
		} catch (CoreException | IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
