package be.seeseemelk.jxe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import be.seeseemelk.jxe.discovery.Flag;
import be.seeseemelk.jxe.types.DecompiledClass;

public class ClassInfoStore
{
	private final Path directory;
	//private final ZipOutputStream zip;
	
	public ClassInfoStore(Path directory)
	{
		this.directory = directory;
	}
	
	public void storeClass(DecompiledClass klass) throws IOException
	{
		FileUtils.deleteQuietly(directory.toFile());
		var target = new File(directory.toFile(), klass.getFullyQualifiedName() + ".classinfo");
		target.getParentFile().mkdirs();
		try (var outputStream = new FileOutputStream(target))
		{
			try
			{
				var xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				var root = xml.createElement("class");
				xml.appendChild(root);
				
				// Write basic information
				setProperty(xml, root, "fqn", klass.getFullyQualifiedName());
				if (klass.getParent().isPresent()) {
					setProperty(xml, root, "parent", klass.getParent().get().getFullyQualifiedName());
				}
				
				// Write field information
				var elements = xml.createElement("fields");
				root.appendChild(elements);
				for (var field : klass.getFields())
				{
					var element = xml.createElement("field");
					elements.appendChild(element);
					
					setProperty(xml, element, "name", field.getName());
					setProperty(xml, element, "accessor", field.getAccessor().toString().toLowerCase());
					setProperty(xml, element, "final", field.isFinal());
					setProperty(xml, element, "static", field.isStatic());
				}
				
				// Write method information
				var methods = xml.createElement("methods");
				root.appendChild(methods);
				for (var method : klass.getMethods())
				{
					var element = xml.createElement("method");
					methods.appendChild(element);
					
					setProperty(xml, element, "name", method.getName());
					setProperty(xml, element, "accessor", method.getAccessor().toString().toLowerCase());
					setProperty(xml, element, "static", method.isStaticMethod());
					setProperty(xml, element, "pure", method.hasFlag(Flag.PURE));
				}
				
				var source = new DOMSource(xml);
				var result = new StreamResult(outputStream);
				
				var transformerFactory = TransformerFactory.newInstance();
				var transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				transformer.transform(source, result);
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				throw new IOException(e);
			}
		}
	}
	
	private void setProperty(Document xml, Element parent, String name, String value)
	{
		var element = xml.createElement(name);
		element.setTextContent(value);
		parent.appendChild(element);
	}
	
	private void setProperty(Document xml, Element parent, String name, boolean value)
	{
		setProperty(xml, parent, name, value ? "true" : "false");
	}
}
