def pytest_addoption(parser):
    parser.addoption("--yapconfig", action="store", default="test_yap.config",
        help="YAP client configuration file, default: test_yap.config")

def pytest_funcarg__yapconfig(request):
    return request.config.option.yapconfig
