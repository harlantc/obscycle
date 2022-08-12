# from setuptools import setup, find_packages
from numpy.distutils.core import setup

setup(
    name="pdfGen",
    version="0.1.0",
    author="Smithsonian Astrophysical Observatory / Chandra X-Ray Center",
    author_email="cxchelp@head.cfa.harvard.edu",
    description="Python module to generate PDFs of CPS proposals.",
    packages=["pdfGen","pdfGen/config"],
    package_data={"pdfGen/config": ['*.json', '*.jpg']}
)
