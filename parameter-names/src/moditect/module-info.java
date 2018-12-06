import com.fasterxml.jackson.databind.Module;

module com.fasterxml.jackson.module.parameternames {
	exports com.fasterxml.jackson.module.paramnames;

	requires com.fasterxml.jackson.databind;

	provides Module with com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
}
