package br.gov.serpro.tools.junit;

import static org.junit.Assert.*;
import static junitx.framework.FileAssert.*;

import java.io.File;

import org.junit.Test;


import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.SourceParser;

public class TestesFuncionais {

    @Test
    public final void testGenerate() {
        final SourceParser parser = new JsmgJavaSourceParser();
        //parser.parse();
    }

}
