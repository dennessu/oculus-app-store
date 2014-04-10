[#macro includeModels models split=", " empty="" prefix="" suffix=""]
[#t][#if models?size > 0]${prefix}[#list models as model][@includeModel model=model/][#if model_has_next]${split}[/#if][/#list]${suffix}[#else]${empty}[/#if]
[/#macro]
